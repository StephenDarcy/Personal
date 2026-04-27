$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$repoRoot = (& git rev-parse --show-toplevel 2>$null)
if (-not $repoRoot) {
    throw "Run this script from inside the repository."
}

Push-Location $repoRoot
try {
    git config core.hooksPath .githooks
    Write-Host "Configured Git hooks path to .githooks"

    if (-not (Get-Command gitleaks -ErrorAction SilentlyContinue)) {
        Write-Warning "gitleaks is not installed. Install it before pushing; the pre-push hook requires it."
    }

    Write-Host "Local security hooks are installed."
}
finally {
    Pop-Location
}
