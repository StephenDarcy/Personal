# Public Content Standards

This repository should read like a professional engineering project owned by a real person, not a transcript of local tooling or planning notes.

## Voice

- Use first-person project language for body copy: `my work`, `my platform`, `I am building`.
- Use the full name `Stephen Darcy` where it is a title, first mention, legal attribution, metadata, or identity signal.
- Avoid repeated third-person phrasing such as possessive owner copy in ordinary prose.
- Keep claims factual, specific, and backed by repository evidence.

## Public Hygiene

- Do not track local tool instructions, prompt files, scratch plans, generated transcripts, or private workflow notes.
- Do not describe the repository as machine-generated.
- Do not include placeholder copy, hype, joke text, or casual caveats in public docs.
- Include only screenshots, logs, fixtures, and examples intentionally prepared for the public site.

## Repository Shape

The public repository should stay structured around the product and engineering system:

- `.github/` for workflows, templates, CODEOWNERS, and repository automation.
- `frontend/` for the public Next.js application.
- `backend/` for the Spring Boot API boundary.
- `contracts/` for OpenAPI contracts and generated-client rules.
- `docs/` for architecture, operations, decisions, and standards.
- `infra/` for deployment infrastructure after scanning is active.

Local-only tooling belongs outside the public history or in ignored paths.
