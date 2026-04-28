# Backend

This directory will contain the public Spring Boot API.

## Ownership

- Framework: Spring Boot.
- Runtime: Java 21.
- Build: Maven.
- Packaging: containerized runtime from the first backend scaffold.

## Boundaries

- Keep the backend as the system of record for server-side behavior.
- Publish OpenAPI documentation for public endpoints.
- Add explicit validation, structured errors, request correlation, and narrow CORS rules with the first API slice.
- Do not store durable visitor-submitted data or add private authenticated areas without an ADR and threat-model update.
- Keep sample configuration obviously fake and free of real secrets, credentials, and account IDs.

## Next Implementation Step

The next backend PR should implement the public runtime status slice from [ADR 0009](../docs/adr/0009-first-backend-vertical-slice.md): Spring Boot scaffold, `GET /api/v1/health`, `GET /api/v1/version`, structured errors, request correlation, narrow CORS, Maven verification, Docker build path, and CI coverage.

Do not add product data, persistence, authentication, contact workflows, analytics collection, or other visitor-submitted data in this slice.
