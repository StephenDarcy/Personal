# ADR 0010: Generated Frontend Client Integration

## Status

Accepted

## Context

ADR 0005 makes OpenAPI the integration boundary between the frontend and backend. The runtime status contract and backend implementation now exist, but the frontend should not handwrite response shapes or start runtime API consumption before the generated-client workflow is clear.

The frontend remains a static-export-first Next.js application. Any backend call from the frontend must be deliberate, public-safe, and compatible with client-side execution unless a later ADR approves server-side frontend behavior.

## Decision

Generated TypeScript API clients will live inside the frontend package under `frontend/src/api/generated/`. Each contract gets its own generated subdirectory; the runtime status client should use `frontend/src/api/generated/runtime-status/`.

Generated output will be committed to the repository as reviewable public source. CI should not silently regenerate clients and continue. Instead, CI should run a drift check that regenerates the client into a temporary location and fails if the committed generated output differs from the OpenAPI source contract.

The first generated runtime status client should be based on the checked-in OpenAPI source contract at `contracts/runtime-status.openapi.json`. The follow-up implementation should pin generator dependencies in `frontend/package.json` and expose these commands:

```powershell
npm --prefix frontend run contracts:generate
npm --prefix frontend run contracts:check
```

`contracts:generate` should regenerate the committed TypeScript client from `contracts/runtime-status.openapi.json`. `contracts:check` should run the same generation into a temporary directory and compare it with `frontend/src/api/generated/runtime-status/` without modifying the working tree.

Repository validation should keep running the existing OpenAPI contract check:

```powershell
node scripts/contracts/check-openapi.mjs
```

Once generated-client drift detection exists, CI should run the drift check for pull requests that change `contracts/`, `frontend/src/api/generated/`, the frontend package manifest or lockfile, or the contract-generation scripts.

Frontend and backend ownership for API changes is:

- Backend owns implemented API behavior, validation, structured errors, correlation behavior, and published OpenAPI accuracy.
- Contract changes are reviewed as the shared boundary and must stay public-safe, with obviously fake examples.
- Frontend owns committed generated TypeScript output and any handwritten adapter code that wraps the generated client for application use.
- A backend API change is incomplete until the source contract, backend behavior, generated frontend client, and drift check all agree.
- A frontend API consumption change must import generated types or generated client entry points instead of duplicating response shapes by hand.

The first safe frontend consumption point for the runtime status API is a future client-side integration point that reads `GET /api/v1/health` and `GET /api/v1/version` after hydration for operational evidence. It should not block the primary static page render, change public content, or introduce persistence, authentication, analytics collection, contact workflows, cloud credentials, or deployment infrastructure.

## Consequences

- API drift becomes visible in pull requests before frontend runtime calls are added.
- Generated output is reviewable and reproducible.
- Static export remains the frontend default while runtime API calls are introduced deliberately.
- The next implementation issue can add generator dependencies, scripts, committed generated output, and CI drift checks without also changing public visual design or wiring live frontend API behavior.
