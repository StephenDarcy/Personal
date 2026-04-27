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

The next backend PR should add the Spring Boot scaffold, health and version endpoints, Maven verification, Docker build path, and CI coverage.
