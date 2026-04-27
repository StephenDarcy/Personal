# ADR 0006: Low-Cost AWS Deployment Path

## Status

Accepted

## Context

The repository is intended to show production judgment while keeping steady-state cost low. The current threat model assumes AWS-oriented deployment patterns, static or prerendered frontend hosting, and a containerized backend API. Production Kubernetes is not justified for v1.

## Decision

Use a low-operations AWS-oriented deployment path:

- Static frontend hosting behind a CDN.
- Containerized backend runtime for the Spring Boot API.
- GitHub Actions deployments through OpenID Connect and least-privilege IAM.
- No long-lived AWS access keys in GitHub secrets.
- No production Kubernetes for v1.

Infrastructure code may be added only after repository scanning covers IaC changes. Dockerfiles may be added only with container scanning in the same or preceding pull request.

## Consequences

- The platform stays cost-conscious and understandable.
- Deployment credentials avoid long-lived shared secrets.
- Kubernetes expertise is not showcased in the v1 production runtime, but this is an intentional cost and complexity tradeoff.
- IaC and container work will require additional CI hardening before merge.
