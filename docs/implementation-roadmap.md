# Implementation Roadmap

This roadmap sequences the repository from foundation to public launch. Each phase should fit in one or more reviewable pull requests, with architecture decisions captured before code depends on them.

## Phase 0: Foundation

Status: Complete enough for application planning.

The repository now has public-safe operating rules, branch manners, CI and security scaffolding, a threat model, and an initial repository operating ADR.

Exit criteria:

- Required foundation documents exist.
- CI and security baseline jobs run without application code.
- Branch protection and repository admin settings are tracked in `docs/github-admin-checklist.md`.
- Future work uses one branch per task.

## Phase 1: Architecture Lock-In

Status: Current phase.

Capture the decisions that will shape the first implementation pass:

- Monorepo application layout.
- Static-export-first frontend architecture.
- Spring Boot backend runtime.
- OpenAPI contract ownership.
- Low-cost AWS-oriented deployment path.
- Observability and release evidence.
- Initial data and authentication boundaries.

Exit criteria:

- ADRs exist for each decision above.
- The threat model reflects the current entry points and boundaries.
- The next implementation PR can scaffold directories without reopening foundational choices.

## Phase 2: Application Skeleton

Status: In progress.

Create the first application structure without building product features yet.

Planned directories:

```text
frontend/     Next.js App Router application
backend/      Spring Boot API application
contracts/    OpenAPI source contracts and generated-client rules
docs/         Architecture, operations, and release evidence
infra/        Infrastructure code, only after IaC scanning is active
```

Exit criteria:

- Top-level ownership directories are tracked and documented.
- Frontend builds in CI.
- Backend verifies in CI.
- Local development commands are documented.
- Docker build paths exist for runtime components.
- No real secrets, credentials, or account IDs are introduced.

Progress:

- Top-level ownership directories are tracked and documented.
- Frontend scaffold exists with static export, linting, type checking, and build scripts.

## Phase 3: Frontend Design System

Build the public-facing design language before feature-heavy pages.

Focus areas:

- Design tokens for color, spacing, type, elevation, and motion.
- Accessible primitives for layout, buttons, links, forms, navigation, and status states.
- Responsive behavior across mobile, tablet, and desktop.
- Visual evidence using screenshots once the frontend exists.

Exit criteria:

- Core UI primitives exist with tests or story-like examples.
- Accessibility checks are part of CI or documented local verification.
- The design avoids generic portfolio templates and stock copy.

## Phase 4: Backend Vertical Slice

Build a small API slice that proves production behavior without premature data storage.

Initial capability:

- Public runtime status slice from [ADR 0009](adr/0009-first-backend-vertical-slice.md).

Initial endpoints:

- `GET /api/v1/health`.
- `GET /api/v1/version`.

Explicitly deferred:

- Public profile or content summary.
- Product data.
- Contact workflows.
- Analytics collection.
- Persistence.
- Authentication.

Required backend traits:

- Explicit request validation.
- Structured error responses.
- OpenAPI documentation.
- Narrow CORS configuration.
- Structured logging and request correlation.
- Rate-limit design before public mutable endpoints exist.

Exit criteria:

- API tests cover the first endpoints.
- OpenAPI output is generated and checked.
- Container image build path exists.
- Runtime configuration uses fake examples only.

## Phase 5: Contract Integration

Connect the frontend to the backend through generated API contracts. ADR 0010 defines the planned generated-client location, committed-output policy, and drift-check workflow.

Exit criteria:

- The OpenAPI contract is the source of truth.
- Frontend clients are generated and checked from the contract.
- CI fails when generated client output is stale.
- Contract examples contain only public-safe fake values.

## Phase 6: Platform Slice

Introduce deployment infrastructure only after scanning is ready.

Preferred direction:

- Static frontend hosting behind a CDN.
- Containerized backend on a low-operations AWS runtime.
- GitHub Actions deployment through OIDC, not long-lived cloud keys.
- No production Kubernetes for v1.

Exit criteria:

- IaC scanning is active before infrastructure files merge.
- Container scanning is active before Dockerfiles merge.
- Deployment permissions are least-privilege.
- Rollback notes and deployment evidence are documented.

## Phase 7: Public Launch Slice

Ship a polished first public experience that demonstrates engineering judgment.

The first launch should emphasize specific, evidence-backed engineering work rather than a generic personal portfolio. Good candidates include architecture notes, project case studies, operational evidence, and carefully selected demos.

Exit criteria:

- Public content is factual and safe.
- The frontend has visual verification evidence.
- The backend exposes only intentional public data.
- Security review covers entry points, dependencies, deployment, and logs.

## Phase 8: Production Hardening

Round out the system after the first launch path works.

Focus areas:

- Secure headers.
- Monitoring and alerts.
- Release notes and rollback checks.
- Dependency, CodeQL, secret, container, and IaC scan evidence.
- Runbooks for deploy, rollback, incident response, and dependency updates.

Exit criteria:

- Launch checklist is repeatable.
- Operational evidence can be reviewed in PRs.
- Architecture decisions remain current with the deployed system.
