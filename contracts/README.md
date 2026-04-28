# Contracts

This directory will contain API contracts and generated-client rules.

## Ownership

- OpenAPI is the boundary between the frontend and backend.
- Backend behavior must match the published contract.
- Frontend API clients should be generated from the contract rather than handwritten.

## Boundaries

- Contract examples must use obviously fake values.
- Contract changes should be reviewed with both frontend and backend impact in mind.
- CI should eventually fail when generated clients or published API output drift from the source contract.

## Next Implementation Step

The first contract PR should add an OpenAPI source file for the public runtime status slice from [ADR 0009](../docs/adr/0009-first-backend-vertical-slice.md), covering:

- `GET /api/v1/health`
- `GET /api/v1/version`
- Shared structured error responses
- Correlation headers
- Public-safe examples

It should also add contract linting, generated-client conventions, and drift checks once the backend and frontend scaffolds exist.
