# Stephen Darcy

My public engineering showcase for a production-grade personal platform.

I am building this repository like a serious product, not a lightweight portfolio. The goal is to demonstrate senior software engineering judgement across frontend craft, backend API design, cloud deployment, security, observability, automation, and long-term maintainability.

## Operating Principles

1. **Public by default, secure always.** Commit only material intentionally prepared for this public repository.
2. **Production-shaped from the first commit.** Every feature should have a clear path through tests, review, CI, deployment, monitoring, and rollback.
3. **Distinctive over generic.** The website should feel specific to my work, with a custom visual language and component system rather than stock portfolio patterns.
4. **Low steady-state cost.** It is acceptable to spend money during setup, but the live site should avoid unnecessary always-on infrastructure.
5. **PR-driven delivery.** Bots and humans open pull requests; nothing merges without passing checks and review.

## Target Architecture

- **Frontend:** Next.js App Router, TypeScript, custom design system, static export or prerendering by default.
- **Backend:** Spring Boot external API, OpenAPI-first contracts, containerized from day one.
- **Cloud:** AWS-oriented deployment path with Docker-based local development and optional local Kubernetes manifests for demonstration.
- **Automation:** GitHub Actions, Dependabot, CodeQL, dependency review, secret scanning, SAST, container scanning, IaC scanning, and release hygiene.
- **Observability:** Structured logs, metrics, tracing, SLO-oriented dashboards, and deployment evidence as the platform matures.

## Repository Map

```text
.github/                 GitHub workflows, issue templates, CODEOWNERS, and automation
backend/                 Spring Boot API application boundary
contracts/               OpenAPI contracts and generated-client rules
docs/                    Architecture, threat model, decisions, and operations notes
frontend/                Next.js public frontend application boundary
infra/                   Deployment infrastructure boundary, gated by IaC scanning
scripts/                 Local automation for security, hygiene, and repository setup
```

Application directories currently document ownership boundaries. Framework scaffolding will be added in follow-up pull requests.

See [docs/implementation-roadmap.md](docs/implementation-roadmap.md) for the phased implementation plan.

## Security Posture

This is a public repository. Treat every file, fixture, log, screenshot, and example as publishable internet-facing material.

- Do not commit secrets or private configuration.
- Use environment variables and managed secret stores for real credentials.
- Keep sample values obviously fake.
- Prefer least-privilege IAM, scoped tokens, and short-lived credentials.
- Review generated code before merge, especially around auth, network calls, dependencies, CI, and deployment.

See [SECURITY.md](SECURITY.md) and [docs/threat-model.md](docs/threat-model.md).

## Delivery Model

The project uses GitHub-native planning:

- Issues track work.
- Labels classify risk and ownership.
- Milestones group roadmap phases.
- Pull requests are the only path to `main`.
- Required CI checks must pass before merge.

Planned milestones:

- `Foundation`
- `Design System`
- `Backend API`
- `Public Launch`
- `Production Hardening`

## Local Development

The frontend scaffold is runnable. Local development will standardize around checked-in commands and containerized dependencies as the backend and contracts are added.

See [docs/local-development.md](docs/local-development.md).

## License

MIT. See [LICENSE](LICENSE).
