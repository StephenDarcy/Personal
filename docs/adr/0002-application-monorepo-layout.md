# ADR 0002: Application Monorepo Layout

## Status

Accepted

## Context

This repository will contain a public Next.js frontend, an external Spring Boot backend API, OpenAPI contracts, operational documentation, and eventually deployment infrastructure. The project should make architecture and review boundaries obvious to contributors and automation.

## Decision

Use a monorepo with explicit top-level ownership boundaries:

- `frontend/` for the Next.js application.
- `backend/` for the Spring Boot API.
- `contracts/` for OpenAPI source contracts and generated-client rules.
- `docs/` for architecture, threat modeling, operations, and release evidence.
- `infra/` for infrastructure code after IaC scanning is active.

The repository will keep frontend and backend runtime code separate. Shared behavior will flow through contracts and generated clients instead of ad hoc shared source packages.

## Consequences

- Pull requests can show cross-system changes in one place when a vertical slice needs them.
- CI can stay selective by detecting which top-level areas changed.
- Contributors get clearer file ownership boundaries.
- The repository may grow larger than separate service repositories, so build caching and focused checks will matter as the codebase matures.
