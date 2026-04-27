# Contributing

This project is my public engineering showcase. Contributions are welcome when they preserve the repository's professional tone, security posture, and production-grade standards.

## Ground Rules

- Treat all content as public.
- Commit only material intentionally prepared for this public repository.
- Open a pull request for every change.
- Use one branch per task.
- Use Git worktrees for concurrent local work.
- Keep changes focused and reviewable.
- Add or update tests when behavior changes.
- Prefer clear, boring implementation choices unless a deliberate technical showcase justifies extra complexity.

## Branch Manners

This repository may have multiple contributors or local automation sessions working in parallel. Good Git hygiene is part of the engineering showcase.

- Branch from an up-to-date `main`.
- Name branches by intent, for example `chore/showcase-foundation`, `feat/frontend-shell`, or `security/dependabot-review`.
- Keep each branch scoped to one issue, ADR, or clearly stated task.
- Prefer a separate Git worktree for every concurrent task.
- Never share the same mutable branch between multiple contributors or tools.
- Never force-push, rebase, squash, reset, or delete a branch owned by someone else.
- Before starting work, run `git status --short --branch`.
- Before committing, review `git diff --stat` and `git diff`.
- Before opening a PR, sync with `main` and rerun the relevant checks.

See [docs/branching-worktrees.md](docs/branching-worktrees.md) for exact commands.

## Pull Request Expectations

Every pull request should explain:

- What changed.
- Why it changed.
- How it was tested.
- Any security, accessibility, operational, or cost implications.

For UI changes, include screenshots or visual evidence once the frontend exists.

For backend changes, include API contract impact and relevant test evidence.

For platform changes, include rollback notes and cost/security implications.

## Commit Style

Use concise, descriptive commit messages. Conventional Commits are preferred once the project has release automation:

```text
feat(frontend): add navigation shell
fix(api): validate contact request payload
docs(security): document threat model update
ci: add container scanning
```

## Security Review

Security-sensitive changes include authentication, authorization, secrets, CI/CD, dependencies, network boundaries, logging, infrastructure, and data handling. These changes require extra care and should be reviewed against [SECURITY.md](SECURITY.md) and [docs/threat-model.md](docs/threat-model.md).

## Code of Conduct

Participation is covered by [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).
