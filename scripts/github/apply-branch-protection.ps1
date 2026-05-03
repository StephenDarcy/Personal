param(
    [string]$Repository = "StephenDarcy/Personal",
    [string]$Branch = "main",
    [switch]$AllowPrivateAttempt,
    [switch]$RequireReview
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
    throw "GitHub CLI is required. Install gh and authenticate with repo admin access."
}

$repoInfo = gh repo view $Repository --json isPrivate,nameWithOwner | ConvertFrom-Json
if ($repoInfo.isPrivate -and -not $AllowPrivateAttempt) {
    throw "Branch protection is not available on this private repository plan. Make $($repoInfo.nameWithOwner) public first, then rerun this script."
}

$requiredChecks = @(
    "repository-hygiene",
    "security-baseline",
    "dependency-review",
    "osv-scan",
    "workflow-lint",
    "workflow-security",
    "codeql",
    "platform-scans",
    "generated-client-drift"
)

$protection = @{
    required_status_checks              = @{
        strict   = $true
        contexts = $requiredChecks
    }
    enforce_admins                      = $true
    required_pull_request_reviews       = if ($RequireReview) {
        @{
            dismiss_stale_reviews           = $true
            require_code_owner_reviews      = $true
            required_approving_review_count = 1
            require_last_push_approval      = $true
        }
    }
    else {
        $null
    }
    restrictions                        = $null
    required_linear_history             = $false
    allow_force_pushes                  = $false
    allow_deletions                     = $false
    block_creations                     = $false
    required_conversation_resolution    = $true
    lock_branch                         = $false
    allow_fork_syncing                  = $true
}

$json = $protection | ConvertTo-Json -Depth 10
$json | gh api `
    --method PUT `
    -H "Accept: application/vnd.github+json" `
    -H "X-GitHub-Api-Version: 2022-11-28" `
    "/repos/$Repository/branches/$Branch/protection" `
    --input -

gh api `
    --method PATCH `
    -H "Accept: application/vnd.github+json" `
    -H "X-GitHub-Api-Version: 2022-11-28" `
    "/repos/$Repository" `
    -f delete_branch_on_merge=true `
    -f allow_auto_merge=false

Write-Host "Applied branch protection to $Repository/$Branch."
Write-Host "Required checks: $($requiredChecks -join ', ')"
if ($RequireReview) {
    Write-Host "Required approving reviews and code owner reviews are enabled."
}
else {
    Write-Host "Solo-maintainer mode is enabled: required reviews are off, but PRs, checks, and conversation resolution remain enforced."
}
