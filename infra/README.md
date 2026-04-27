# Infrastructure

This directory is reserved for deployment infrastructure.

## Ownership

- Preferred direction: low-cost AWS-oriented deployment.
- Frontend: static hosting behind a CDN.
- Backend: containerized runtime.
- Deployment authentication: GitHub Actions OpenID Connect with least-privilege IAM.

## Boundaries

- Do not add infrastructure-as-code files until IaC scanning is active in CI.
- Do not add long-lived cloud access keys, real account IDs, or private deployment details.
- Production Kubernetes is outside the v1 runtime unless a later ADR approves it.

## Next Implementation Step

The first infrastructure PR should add IaC scanning before or alongside any real infrastructure files.
