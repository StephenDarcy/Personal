# Frontend

This directory contains the public Next.js application.

## Ownership

- Framework: Next.js App Router.
- Language: TypeScript with strict checking.
- Rendering default: static export or prerendering.
- Design direction: custom design system specific to Stephen Darcy.

## Boundaries

- Do not add server-only frontend behavior unless an ADR approves the runtime change.
- Consume backend APIs through generated clients from `contracts/`.
- Keep generated clients under `src/api/generated/` and do not handwrite backend response shapes.
- Keep public examples and fixtures free of secrets, credentials, and real account IDs.
- Use visual verification evidence for meaningful UI changes once the application exists.

## Generated API Clients

ADR 0010 places generated TypeScript clients in `src/api/generated/`. The runtime status client should be generated into `src/api/generated/runtime-status/` from `../contracts/runtime-status.openapi.json`.

Generated output should be committed. Drift checks should regenerate the client into a temporary location and compare it with the committed output instead of silently updating files in CI.

Planned commands for the follow-up implementation:

```powershell
npm run contracts:generate
npm run contracts:check
```

The first safe runtime status consumption point is a future client-side operational evidence integration that reads `/api/v1/health` and `/api/v1/version` after hydration. Do not wire live backend calls into the public app until a follow-up issue explicitly expands the scope.

## Next Implementation Step

The next frontend PR should grow the design system primitives and add visual regression evidence for meaningful UI changes.

## Local Commands

```powershell
node --version # Node 22.13.0 or newer
npm install
npm run lint
npm run typecheck
npm run build
npm run accessibility
```
