# Contracts

This directory contains API contracts and generated-client rules.

## Ownership

- OpenAPI is the boundary between the frontend and backend.
- Backend behavior must match the published contract.
- Frontend API clients should be generated from the contract rather than handwritten.
- Generated frontend client output belongs to the frontend package under `frontend/src/api/generated/`.

## Boundaries

- Contract examples must use obviously fake values.
- Contract changes should be reviewed with both frontend and backend impact in mind.
- Generated clients should be committed and checked for drift against the source contract in CI.

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

## Generated Frontend Client

ADR 0010 defines the generated frontend client workflow. The runtime status client is generated from [runtime-status.openapi.json](runtime-status.openapi.json) into:

```text
frontend/src/api/generated/runtime-status/
```

Generated output is committed as reviewable public source. The frontend package pins `@hey-api/openapi-ts` as the generator dependency and exposes:

```powershell
npm --prefix frontend run contracts:generate
npm --prefix frontend run contracts:check
```

`contracts:generate` regenerates the committed TypeScript client. `contracts:check` regenerates the same client into a temporary location and fails when the committed output differs from the source contract. CI enforcement for that drift check is tracked separately from the local workflow.
