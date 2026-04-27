# GitHub Admin Checklist

These settings require repository admin access through GitHub UI, GitHub CLI, or GitHub API. They are listed here so setup remains auditable from the public repo.

## Repository Settings

- Keep repository visibility public.
- Enable Issues.
- Enable Projects.
- Enable Discussions only if there is a clear moderation plan.
- Prefer squash merge or rebase merge; disable merge commits if the history starts getting noisy.
- Disable auto-merge for this repository.
- Enable automatically delete head branches after merge.

## Branch Protection for `main`

Solo-maintainer baseline:

- Pull request before merge.
- Conversation resolution.
- Status checks to pass.
- Branch up to date before merge.
- No force pushes.
- No branch deletion.
- Include administrators.

Do not require approving reviews or code owner review while this is a solo-maintained repository. That creates a fake approval loop where the only available reviewer is the repository owner. Keep advisory review useful, but let the enforced gate be CI, security checks, conversation resolution, and public evidence.

Re-enable required approving reviews and code owner review after adding a trusted human collaborator who can review independently.

Initial required status checks:

- `repository-hygiene`
- `security-baseline`
- `codeql`

CodeQL must run and upload results once analyzable application code exists. If the repository is private and `codeql` fails because code scanning is not enabled, fix the repository security settings rather than weakening the workflow. Before public launch, confirm the `codeql` job uploads results successfully.

Add `dependency-review` only after the repository is public or GitHub confirms Dependency graph plus GitHub Advanced Security support for the private repository. While the repository is private without that support, GitHub's dependency review action fails before it can analyze dependencies.

Private-repo strict substitutes that should be required now:

- `osv-scan`
- `workflow-lint`
- `workflow-security`

Update the required check list as app-specific CI jobs are added.

After the repository is public, apply the baseline protection from a local admin checkout:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/github/apply-branch-protection.ps1
```

To switch to collaborator mode later:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/github/apply-branch-protection.ps1 -RequireReview
```

The script intentionally stops while this repository is private unless `-AllowPrivateAttempt` is passed, because the current repository plan does not expose branch protection for private repositories.

## Security Features

Enable:

- GitHub Advanced Security if available.
- Secret scanning.
- Secret scanning push protection.
- Dependabot alerts.
- Dependabot security updates.
- Private vulnerability reporting.
- CodeQL default setup or this repository's advanced workflow.

## Pull Request Evidence

Require pull request descriptions to include:

- A concise summary of the change and why it exists.
- Verification commands and results.
- Security and operations impact.
- Desktop and mobile screenshots for UI changes.
- Before/after screenshots when an existing screen or visual state changes.

If a UI-adjacent change has no visible impact, the pull request should say why screenshots are not relevant.

## Optional Quality Gates

SonarQube Cloud can be useful once the repository has stable frontend and backend code, because it can decorate pull requests with quality-gate results and issues. Do not add it as a required check until the tradeoffs are accepted:

- It introduces another external service and repository integration.
- CI-based analysis normally needs Sonar project configuration and a scoped token.
- The existing baseline already has CodeQL, dependency review, OSV, workflow linting, workflow security scanning, secret scanning, and package audits.

Recommended order:

1. Keep CodeQL and existing security checks required now.
2. Add a lightweight independent review habit for every non-trivial PR.
3. Revisit SonarQube Cloud after frontend and backend test coverage exist, so quality-gate findings have enough signal to be worth making required.

## Local Developer Tools

Install these locally for pre-push checks:

- `gitleaks` for secret scanning.
- `actionlint` for GitHub Actions linting.

The GitHub workflows still run server-side checks, but local tools catch mistakes before they reach the public remote.

Install the repository hooks before pushing:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/security/install-hooks.ps1
```

Before making the repository public, run:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/security/preflight.ps1 -Scope Repository -RequireGitleaks
```

## First PR Merge Order

For the bootstrap pull request:

1. Confirm local `gitleaks` and `actionlint` pass.
2. Push the branch.
3. Open the pull request as draft while still polishing.
4. Mark the pull request ready for review.
5. Let CI and review run.
6. Address findings.
7. Merge after checks and review pass.
8. Sync local `main`.

## Labels

Create these labels:

- `security`
- `architecture`
- `frontend`
- `backend`
- `platform`
- `ci`
- `docs`
- `design-system`
- `good-first-task`
- `blocked`
- `adr-needed`

## Milestones

Create these milestones:

- `Foundation`
- `Design System`
- `Backend API`
- `Public Launch`
- `Production Hardening`

## Project Board

Create a GitHub Project with these views or columns:

- Backlog
- Ready
- In Progress
- Review
- Blocked
- Done
- Security

Suggested custom fields:

- Area: Frontend, Backend, Platform, Security, Design, Docs.
- Risk: Low, Medium, High, Critical.
- Milestone.
- Status.
