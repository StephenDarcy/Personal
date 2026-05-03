param(
    [ValidateSet("Staged", "Repository")]
    [string]$Scope = "Repository",

    [switch]$RequireGitleaks,
    [switch]$SkipDependencyAudit
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

function Write-Section {
    param([string]$Message)
    Write-Host ""
    Write-Host "==> $Message"
}

function Add-Finding {
    param(
        [System.Collections.Generic.List[string]]$Findings,
        [string]$Path,
        [string]$Reason
    )
    $Findings.Add("${Path}: ${Reason}") | Out-Null
}

function Get-RepoRoot {
    $root = (& git rev-parse --show-toplevel 2>$null)
    if (-not $root) {
        throw "This script must be run inside a Git repository."
    }
    return (Resolve-Path -LiteralPath $root).Path
}

function Get-CandidateFiles {
    param(
        [string]$Root,
        [string]$Mode
    )

    if ($Mode -eq "Staged") {
        $files = & git diff --cached --name-only --diff-filter=ACMR
    }
    else {
        $files = & git ls-files
    }

    $files |
        Where-Object { $_ -and ($_ -notmatch '(^|/)(node_modules|\.next|out|dist|target|build|coverage)(/|$)') } |
        ForEach-Object {
            $fullPath = Join-Path $Root ($_ -replace '/', [System.IO.Path]::DirectorySeparatorChar)
            if (Test-Path -LiteralPath $fullPath -PathType Leaf) {
                [PSCustomObject]@{
                    RelativePath = $_
                    FullPath     = $fullPath
                }
            }
        }
}

function Test-TextFile {
    param([string]$Path)

    $bytes = [System.IO.File]::ReadAllBytes($Path)
    if ($bytes.Length -eq 0) {
        return $true
    }
    if ($bytes.Length -gt 2MB) {
        return $false
    }
    return -not ($bytes[0..([Math]::Min($bytes.Length - 1, 4095))] -contains 0)
}

function Invoke-PatternScan {
    param(
        [array]$Files
    )

    Write-Section "Scanning tracked content for secrets and personal identifier patterns"

    $findings = [System.Collections.Generic.List[string]]::new()
    $sensitivePathPattern = '(^|/)(\.env($|\.)|id_(rsa|dsa|ecdsa|ed25519)$|.*\.(pem|p12|pfx|key|keystore|jks)$)'
    $patterns = @(
        @{ Name = "AWS access key id"; Pattern = 'AKIA[0-9A-Z]{16}' },
        @{ Name = "private key block"; Pattern = '-----BEGIN (RSA|OPENSSH|EC|DSA)? ?PRIVATE KEY-----' },
        @{ Name = "GitHub token"; Pattern = 'gh[pousr]_[A-Za-z0-9_]{36,255}' },
        @{ Name = "API key token"; Pattern = 'sk-[A-Za-z0-9_-]{32,}' },
        @{ Name = "Slack token"; Pattern = 'xox[baprs]-[A-Za-z0-9-]{20,}' },
        @{ Name = "Google API key"; Pattern = 'AIza[0-9A-Za-z_-]{35}' },
        @{ Name = "non-example email address"; Pattern = '\b[A-Z0-9._%+-]+@(?!example\.com\b|example\.org\b|example\.net\b|users\.noreply\.github\.com\b)[A-Z0-9.-]+\.[A-Z]{2,}\b' },
        @{ Name = "personal identifier field"; Pattern = '(?i)\b(ssn|social security|passport|date of birth|dob|home address|personal phone|mobile number|pps number)\b\s*[:=]\s*\S+' }
    )

    foreach ($file in $Files) {
        $normalized = $file.RelativePath -replace '\\', '/'
        if ($normalized -match $sensitivePathPattern) {
            Add-Finding -Findings $findings -Path $file.RelativePath -Reason "sensitive file path is tracked or staged"
            continue
        }

        if (-not (Test-TextFile -Path $file.FullPath)) {
            continue
        }

        $content = Get-Content -LiteralPath $file.FullPath -Raw
        foreach ($check in $patterns) {
            if ($content -match $check.Pattern) {
                Add-Finding -Findings $findings -Path $file.RelativePath -Reason $check.Name
            }
        }
    }

    if ($findings.Count -gt 0) {
        Write-Error ("Public-safety scan failed:`n" + ($findings -join "`n"))
    }

    Write-Host "Pattern scan passed."
}

function Invoke-PublicContentHygiene {
    param(
        [array]$Files
    )

    Write-Section "Checking public content hygiene"

    $findings = [System.Collections.Generic.List[string]]::new()
    $blockedPathPattern = '(^|/)(\.agents|\.codex)(/|$)|(^|/)\.github/codex(/|$)|(^|/)AGENTS\.md$|(^|/)(prompt|prompts)(/|$)|(^|/).*prompt.*\.(md|txt|toml|ya?ml)$'
    $policyAllowList = @(
        'docs/content-standards.md',
        'scripts/security/preflight.ps1',
        '.gitignore'
    )
    $toneScanExcludedPathPattern = '(^|/)(scripts|\.github/workflows|frontend/src/api/generated)(/|$)'
    $automationLanguagePatterns = @(
        @{ Name = "local AI/tooling reference"; Pattern = '(?i)\b(Codex|ChatGPT|Claude|OpenAI|LLM|AI[- ]?assisted|AI[- ]?generated|AI slop)\b' },
        @{ Name = "assistant instruction language"; Pattern = '(?i)\b(system prompt|developer prompt|prompt injection|assistant instructions?|agent instructions?)\b' },
        @{ Name = "public repo as generated output"; Pattern = '(?i)\b((generated|created|written) by (an? )?(AI|LLM|ChatGPT|Claude|OpenAI)|AI wrote)\b' }
    )
    $tonePatterns = @(
        @{ Name = "third-person possessive owner phrasing"; Pattern = "\bStephen Darcy's\b" },
        @{ Name = "placeholder copy"; Pattern = '(?i)\b(TODO|TBD|lorem ipsum|coming soon|under construction)\b' },
        @{ Name = "draft caveat"; Pattern = '(?i)\b(this is just a demo|toy project|throwaway|quick hack)\b' }
    )

    foreach ($file in $Files) {
        $normalized = ($file.RelativePath -replace '\\', '/')
        if ($normalized -match $blockedPathPattern) {
            Add-Finding -Findings $findings -Path $file.RelativePath -Reason "local tooling, prompt, or instruction file must stay untracked"
            continue
        }

        if (-not (Test-TextFile -Path $file.FullPath)) {
            continue
        }

        $content = Get-Content -LiteralPath $file.FullPath -Raw
        $isPolicyFile = $policyAllowList -contains $normalized

        if (-not $isPolicyFile) {
            foreach ($check in $automationLanguagePatterns) {
                if ($content -match $check.Pattern) {
                    Add-Finding -Findings $findings -Path $file.RelativePath -Reason $check.Name
                }
            }
        }

        if (-not $isPolicyFile -and $normalized -notmatch $toneScanExcludedPathPattern) {
            foreach ($check in $tonePatterns) {
                if ($content -match $check.Pattern) {
                    Add-Finding -Findings $findings -Path $file.RelativePath -Reason $check.Name
                }
            }
        }
    }

    if ($findings.Count -gt 0) {
        Write-Error ("Public-content hygiene failed:`n" + ($findings -join "`n"))
    }

    Write-Host "Public-content hygiene passed."
}

function Invoke-Gitleaks {
    param(
        [string]$Root,
        [string]$Mode,
        [bool]$Required
    )

    Write-Section "Running gitleaks"
    $gitleaks = Get-Command gitleaks -ErrorAction SilentlyContinue
    if (-not $gitleaks) {
        if ($Required) {
            throw "gitleaks is required for this check. Install it before pushing."
        }
        Write-Warning "gitleaks is not installed; built-in pattern checks still ran."
        return
    }

    Push-Location $Root
    try {
        if ($Mode -eq "Staged") {
            & gitleaks protect --staged --redact --no-banner
        }
        else {
            & gitleaks detect --source . --redact --no-banner
        }
        if ($LASTEXITCODE -ne 0) {
            throw "gitleaks reported findings."
        }
    }
    finally {
        Pop-Location
    }
}

function Invoke-DependencyAudit {
    param([string]$Root)

    if ($SkipDependencyAudit) {
        return
    }

    $frontendPackage = Join-Path $Root "frontend/package-lock.json"
    if (-not (Test-Path -LiteralPath $frontendPackage)) {
        return
    }

    Write-Section "Running frontend npm audit"
    Push-Location (Join-Path $Root "frontend")
    try {
        & npm audit --audit-level=moderate
        if ($LASTEXITCODE -ne 0) {
            throw "npm audit reported moderate or higher vulnerabilities."
        }
    }
    finally {
        Pop-Location
    }
}

$repoRoot = Get-RepoRoot
Write-Host "Security preflight scope: $Scope"
Write-Host "Repository: $repoRoot"

$candidateFiles = @(Get-CandidateFiles -Root $repoRoot -Mode $Scope)
if ($candidateFiles.Count -eq 0) {
    Write-Host "No candidate files to scan."
    exit 0
}

Invoke-PatternScan -Files $candidateFiles
Invoke-PublicContentHygiene -Files $candidateFiles
Invoke-Gitleaks -Root $repoRoot -Mode $Scope -Required ([bool]$RequireGitleaks)
Invoke-DependencyAudit -Root $repoRoot

Write-Host ""
Write-Host "Security and public-content preflight passed."
