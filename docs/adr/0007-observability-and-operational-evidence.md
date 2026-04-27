# ADR 0007: Observability and Operational Evidence

## Status

Accepted

## Context

The project should demonstrate production-shaped behavior, not just working code. Public-facing systems need enough observability to understand deployments, failures, latency, and dependency risk without leaking sensitive data.

## Decision

Design observability as part of each runtime slice:

- Backend logs should be structured and avoid sensitive values.
- API requests should have correlation identifiers.
- Metrics should cover health, latency, errors, and runtime basics.
- Tracing should be OpenTelemetry-compatible when added.
- Release evidence should document build, test, scan, deploy, and rollback status.

Observability examples and screenshots must use public-safe local demo content and must not contain real secrets, real account IDs, or raw operational logs.

## Consequences

- Operational quality becomes visible in pull requests and docs.
- Logging and telemetry require review as a security-sensitive surface.
- The first implementation slices may include more plumbing than a basic personal site, but that is consistent with the repository intent.
