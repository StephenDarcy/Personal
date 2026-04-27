# Security Policy

## Public Repository Rule

This repository is public. Never commit secrets, credentials, private keys, tokens, real production configuration, account IDs, or unsafe exploit material.

Use obviously fake examples such as `example.invalid`, `changeme`, or `00000000-0000-0000-0000-000000000000`.

## Supported Scope

Security reports may cover:

- Website frontend code and build configuration.
- Spring Boot backend code and API contracts.
- Infrastructure as code.
- GitHub Actions and repository automation.
- Container images and dependency supply chain.
- Documentation or examples that could cause insecure deployment.

## Reporting a Vulnerability

Please do not open a public issue for suspected vulnerabilities.

Use GitHub private vulnerability reporting if it is enabled for this repository. If it is not enabled yet, contact the repository owner through the public GitHub profile and share only the minimum information needed to establish a secure channel.

## Response Expectations

This is a personal public project, but security issues are treated seriously:

- Critical issues: acknowledge as soon as practical and prioritize immediately.
- High issues: prioritize ahead of feature work.
- Medium and low issues: triage into the normal backlog.

Fixes should land through pull requests with tests or verification evidence where practical.

## Automation

The repository is designed to use:

- GitHub secret scanning and push protection.
- Dependabot security updates.
- CodeQL.
- Dependency review.
- Secret scanning in CI.
- Container and IaC scanning once those assets exist.

Bots may open remediation pull requests, but auto-merge is disabled by policy.
