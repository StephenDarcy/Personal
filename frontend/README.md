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
- Keep public examples and fixtures free of secrets, credentials, and real account IDs.
- Use visual verification evidence for meaningful UI changes once the application exists.

## Next Implementation Step

The next frontend PR should grow the design system primitives and add visual regression evidence for meaningful UI changes.

## Local Commands

```powershell
node --version # Node 22.13.0 or newer
npm install
npm run lint
npm run typecheck
npm run build
```
