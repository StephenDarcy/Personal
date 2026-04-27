# ADR 0001: Repository Operating Model

## Status

Accepted

## Context

This public repository is intended to become a showcase of my software engineering practice. It should demonstrate production judgement before application code exists.

## Decision

The repository will use a GitHub-native, security-first operating model:

- Public documentation and policies are committed from the start.
- All changes flow through pull requests.
- Bots open remediation PRs but do not auto-merge.
- Local development automation is allowed, but agent-specific configuration stays untracked.
- CI and security checks are introduced before app scaffolding.
- The live architecture will prefer low steady-state cost unless a higher-cost component is explicitly justified.

## Consequences

- Early setup is heavier than a normal personal website.
- Review, automation, and security controls are visible to visitors.
- Future app decisions have a clear baseline for consistency.
