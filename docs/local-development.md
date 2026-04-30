# Local Development

This document tracks the expected local development shape as runtime components are introduced.

## Current State

The repository has a runnable frontend scaffold, an initial OpenAPI source contract, and the first Spring Boot runtime status API slice.

## Local Commands

Local development provides checked-in commands for:

- Frontend install, lint, typecheck, and build.
- Backend verify, test, and package.
- Contract checks and generated-client drift checks.
- Container build and local runtime smoke checks.

Frontend commands:

```powershell
Set-Location frontend
node --version # Node 22.13.0 or newer
npm install
npm run lint
npm run typecheck
npm run build
```

Contract command:

```powershell
node scripts/contracts/check-openapi.mjs
```

Backend command:

```powershell
cd backend
mvn --batch-mode verify
```

Backend container smoke command:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/backend/smoke-container.ps1 -ImageName personal-api:local
```

The smoke command builds the Spring Boot buildpack image, starts it on `localhost:18080`, checks `/api/v1/health` and `/api/v1/version`, and removes the container when it finishes. It requires Docker or a Docker-compatible local runtime.

CI runs backend Maven verification and backend container verification for backend-affecting changes. The container job builds the buildpack image, scans it with Trivy, and runs the same smoke script against the already-built image.

## Security Rules

- Keep local secrets in ignored environment files or managed secret stores.
- Commit only fake examples such as `.env.example` when examples are needed.
- Commit only logs, screenshots, traces, or fixtures intentionally prepared for public review.
- Treat generated files as public artifacts before adding them to source control.

## Local Security Preflight

Install local Git hooks before pushing:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/security/install-hooks.ps1
```

Run the repository preflight manually:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/security/preflight.ps1 -Scope Repository -RequireGitleaks
```

The preflight checks tracked or staged files for sensitive paths, common secret patterns, non-example email addresses, personal identifier fields, local tooling files that should stay untracked, placeholder copy, and public-tone drift. When `gitleaks` is installed, it also runs a dedicated secret scan. The pre-push hook requires `gitleaks` so pushes do not rely only on the built-in pattern checks.

The repository preflight also runs the frontend npm audit when `frontend/package-lock.json` exists. That audit is part of the repository-level security check even when frontend source files are otherwise untouched.

See [docs/content-standards.md](content-standards.md) for the public content rules enforced by the local preflight.
