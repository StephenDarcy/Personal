# Branching and Worktree Guide

This repository may have multiple contributors or local automation sessions working at the same time. The default is one task, one branch, one pull request.

## Rules

- Do not work directly on `main`.
- Do not let multiple contributors or tools share a mutable branch.
- Use a separate Git worktree for each concurrent task.
- Keep branches small and named by intent.
- Commit only files related to the task.
- Never force-push, rebase, squash, reset, delete, or clean up a branch owned by someone else unless explicitly asked.
- If unexpected changes appear, assume they are someone else's work and stop before overwriting them.

## Starting a Normal Task

```powershell
git fetch origin
git switch main
git pull --ff-only origin main
git switch -c chore/descriptive-task-name
git status --short --branch
```

Use branch prefixes consistently:

- `feat/` for product features.
- `fix/` for bug fixes.
- `docs/` for documentation-only changes.
- `chore/` for repo maintenance.
- `ci/` for workflow and automation changes.
- `security/` for hardening or vulnerability remediation.

## Starting Parallel Work with Git Worktrees

Create a sibling worktree for each concurrent task:

```powershell
git fetch origin
git worktree add ..\Personal-task-name -b chore/descriptive-task-name origin/main
Set-Location ..\Personal-task-name
git status --short --branch
```

Run each concurrent local session from its own worktree directory. This keeps file edits, local build outputs, and branch state isolated.

## Finishing a Task

```powershell
git status --short --branch
git diff --stat
git diff
git add .
git commit -m "chore: describe the completed task"
git push -u origin chore/descriptive-task-name
```

Then open a pull request into `main`.

## Cleaning Up a Worktree After Merge

Only clean up your own completed worktree after the PR has merged:

```powershell
Set-Location ..\Personal
git fetch origin --prune
git worktree list
git worktree remove ..\Personal-task-name
git branch -d chore/descriptive-task-name
```

If Git says the branch is not fully merged, do not force-delete it. Investigate first.

## Syncing Long-Running Branches

Prefer merging `main` into the branch for shared branches:

```powershell
git fetch origin
git merge origin/main
```

Avoid rebasing shared branches. Rebase only private, unpublished branches you own.
