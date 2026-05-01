[CmdletBinding()]
param(
    [string]$ImageName = "personal-api:local",
    [ValidateRange(1024, 65535)]
    [int]$HostPort = 18080,
    [string]$ContainerName = "",
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

function Write-Section {
    param([string]$Message)
    Write-Host ""
    Write-Host "==> $Message"
}

function Get-RepoRoot {
    $root = (& git rev-parse --show-toplevel 2>$null)
    if (-not $root) {
        throw "This script must be run inside the repository."
    }
    return (Resolve-Path -LiteralPath $root).Path
}

function Assert-DockerAvailable {
    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        throw "Docker or a Docker-compatible CLI is required for backend container smoke checks."
    }
    & docker info *> $null
    if ($LASTEXITCODE -ne 0) {
        throw "Docker is installed but is not available. Start the Docker-compatible runtime and try again."
    }
}

function Invoke-SmokeRequest {
    param(
        [string]$Path,
        [scriptblock]$Validate
    )

    $uri = "http://localhost:${HostPort}${Path}"
    $headers = @{
        Accept = "application/json"
        "X-Correlation-ID" = "req_20260115_103000_demo"
    }
    $response = Invoke-WebRequest -UseBasicParsing -Uri $uri -Headers $headers
    if ($response.StatusCode -ne 200) {
        throw "${Path} returned HTTP $($response.StatusCode)."
    }
    $body = $response.Content | ConvertFrom-Json
    & $Validate $body
    Write-Host "${Path} passed."
}

$repoRoot = Get-RepoRoot
if ([string]::IsNullOrWhiteSpace($ContainerName)) {
    $ContainerName = "personal-api-smoke-$PID"
}

Assert-DockerAvailable

if (-not $SkipBuild) {
    Write-Section "Building backend image"
    Push-Location (Join-Path $repoRoot "backend")
    try {
        & mvn --batch-mode spring-boot:build-image "-Dspring-boot.build-image.imageName=$ImageName"
        if ($LASTEXITCODE -ne 0) {
            throw "Backend image build failed."
        }
    }
    finally {
        Pop-Location
    }
}

Write-Section "Starting backend container"
& docker rm -f $ContainerName *> $null
& docker run --rm --detach --name $ContainerName --publish "$($HostPort):8080" $ImageName
if ($LASTEXITCODE -ne 0) {
    throw "Backend container failed to start."
}

try {
    Write-Section "Waiting for runtime status API"
    $ready = $false
    for ($attempt = 1; $attempt -le 30; $attempt++) {
        try {
            $health = Invoke-WebRequest -UseBasicParsing -Uri "http://localhost:${HostPort}/api/v1/health" -Headers @{ Accept = "application/json" }
            if ($health.StatusCode -eq 200) {
                $ready = $true
                break
            }
        }
        catch {
            Start-Sleep -Seconds 2
        }
    }

    if (-not $ready) {
        & docker logs $ContainerName
        throw "Backend container did not become ready."
    }

    Write-Section "Checking runtime status endpoints"
    Invoke-SmokeRequest -Path "/api/v1/health" -Validate {
        param($Body)
        if ($Body.status -ne "ready") {
            throw "Health response did not report ready."
        }
        if ($Body.serviceName -ne "personal-api") {
            throw "Health response used an unexpected serviceName."
        }
        if (-not $Body.checkedAt) {
            throw "Health response did not include checkedAt."
        }
    }

    Invoke-SmokeRequest -Path "/api/v1/version" -Validate {
        param($Body)
        if ($Body.serviceName -ne "personal-api") {
            throw "Version response used an unexpected serviceName."
        }
        if ($Body.version -notmatch '^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)') {
            throw "Version response did not include a semantic version."
        }
        if ($Body.commitSha -notmatch '^[a-f0-9]{40}$') {
            throw "Version response did not include a public-safe full commit SHA."
        }
        if (-not $Body.buildTimestamp) {
            throw "Version response did not include buildTimestamp."
        }
    }

    Write-Section "Backend container smoke check passed"
}
finally {
    Write-Section "Stopping backend container"
    & docker rm -f $ContainerName *> $null
}
