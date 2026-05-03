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

ADR 0010 places generated TypeScript clients in `src/api/generated/`. The runtime status client is generated into `src/api/generated/runtime-status/` from `../contracts/runtime-status.openapi.json`.

Generated output is committed as reviewable public source. The frontend package pins `@hey-api/openapi-ts` and uses the generator's fetch client output for this contract.

Regenerate the committed client:

```powershell
npm run contracts:generate
```

Check for stale committed output without modifying the working tree:

```powershell
npm run contracts:check
```

The drift check regenerates into a temporary directory and compares the temporary output with `src/api/generated/runtime-status/`.

Generated files are included in TypeScript checking and production builds. ESLint ignores `src/api/generated/` because the generator owns that source formatting.

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
npm run contracts:generate
npm run contracts:check
```
