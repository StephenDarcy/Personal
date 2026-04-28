# Contracts

This directory contains API contracts and generated-client rules.

## Ownership

- OpenAPI is the boundary between the frontend and backend.
- Backend behavior must match the published contract.
- Frontend API clients should be generated from the contract rather than handwritten.

## Boundaries

- Contract examples must use obviously fake values.
- Contract changes should be reviewed with both frontend and backend impact in mind.
- CI should eventually fail when generated clients or published API output drift from the source contract.

## Source Contracts

The first source contract is [runtime-status.openapi.json](runtime-status.openapi.json), which defines the public runtime status slice from [ADR 0009](../docs/adr/0009-first-backend-vertical-slice.md):

- `GET /api/v1/health`
- `GET /api/v1/version`
- Shared structured error responses
- Correlation headers
- Public-safe examples

Both endpoints are read-only. They accept no request body, no query parameters, and no path parameters.

## Correlation Headers

Runtime status requests may include `X-Correlation-ID`. If the request omits it, the backend should generate a correlation identifier. Responses should include the accepted or generated value in the `X-Correlation-ID` response header. Structured error responses should also include the same value in `correlationId`.

Invalid correlation header values should return a public-safe structured `400` response without echoing unsafe input.

## Local Checks

Run the dependency-free contract check from the repository root:

```powershell
node scripts/contracts/check-openapi.mjs
```

Generated-client conventions and drift checks should be added once frontend client generation exists.
