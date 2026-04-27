# Threat Model

## Project Overview

This repository will host a public personal website for Stephen Darcy and an external backend API built by this project. The system is public internet-facing and should be treated as production software even while it is being developed as a showcase.

The initial goal is low steady-state cost with strong engineering signals: static or prerendered frontend hosting, a containerized Spring Boot API, GitHub-native automation, and AWS-oriented deployment patterns.

## Assets

- Public website content and design system.
- Backend API code and OpenAPI contracts.
- Build, deployment, and repository automation.
- Cloud infrastructure configuration.
- Observability configuration and operational runbooks.
- Any future contact form messages, analytics events, or visitor-submitted data.

## Entry Points

- Public website routes.
- Browser-to-API requests.
- Backend API endpoints.
- GitHub pull requests, issues, Actions, and dependency update bots.
- Container image build and deployment pipeline.
- Infrastructure provisioning pipeline.

## Trust Boundaries

- Public browser to website hosting.
- Public browser to backend API.
- CI runner to repository contents and GitHub token.
- CI runner to package registries and container registries.
- Deployment pipeline to AWS.
- Backend API to any managed services, databases, queues, or observability providers.

## Primary Risks

- Secret exposure through commits, logs, fixtures, screenshots, or generated examples.
- Dependency compromise in npm, Maven, GitHub Actions, or container base images.
- Overprivileged GitHub Actions or cloud credentials.
- Unsafe automation if bots consume issue, pull request, or commit content.
- Cross-site scripting or unsafe rendering in the public website.
- API abuse through missing validation, rate limiting, auth boundaries, or CORS mistakes.
- Sensitive data leakage through logs, metrics, traces, errors, or analytics.
- Misconfigured object storage, CDN, DNS, TLS, IAM, or container runtime settings.

## Security Requirements

- Never commit real secrets, credentials, or account IDs.
- Use least privilege for GitHub Actions, cloud IAM, and deployment credentials.
- Keep bot remediation PR-only; no automatic merges.
- Require CI checks and review before `main`.
- Validate and encode all untrusted input.
- Keep CORS explicit and narrow.
- Use secure headers on frontend hosting and API responses.
- Prefer generated typed clients from OpenAPI over handwritten API drift.
- Record security-relevant architecture decisions in `docs/adr/`.

## Review Focus

Prioritize review on:

- CI/CD permissions and secret handling.
- API validation, auth, rate limiting, and CORS.
- Dependency and container supply chain.
- Infrastructure exposure and IAM boundaries.
- Logging and observability data hygiene.
- Any generated or automated code changes touching security-sensitive areas.

## Current Assumptions

- No production user accounts exist yet.
- No real visitor-submitted data is stored yet.
- The frontend will default to static export or prerendering.
- The backend will be an external Spring Boot API on Java 21.
- OpenAPI will define the frontend/backend contract boundary.
- Kubernetes is not part of the v1 production runtime unless later justified.
- V1 avoids durable visitor-submitted data and authenticated private areas unless a later ADR approves them.

See `docs/implementation-roadmap.md` and `docs/adr/` for accepted implementation decisions.
