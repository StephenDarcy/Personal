# ADR 0005: OpenAPI Contract Ownership

## Status

Accepted

## Context

The frontend and backend need a reliable integration boundary. Handwritten clients and undocumented response shapes create drift, especially when multiple branches change related areas.

## Decision

Use OpenAPI as the public API contract boundary.

The backend owns implemented behavior and must publish OpenAPI output. The repository will keep contract source or generated contract artifacts under `contracts/`, with CI checks that prevent stale generated clients once frontend integration exists. The frontend should consume generated TypeScript clients instead of handwritten API wrappers for backend endpoints.

## Consequences

- API changes become reviewable as contract changes.
- Frontend and backend drift can be caught in CI.
- Contract examples must be public-safe and use obviously fake values.
- Initial setup is heavier because contract linting and generation must be wired before the first real API integration.
