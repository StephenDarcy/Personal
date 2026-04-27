# ADR 0004: Backend Runtime and API Boundary

## Status

Accepted

## Context

The backend is the system of record for server-side behavior. It should demonstrate professional API design, validation, error handling, observability, and containerized operation without adding avoidable infrastructure complexity.

## Decision

Build the backend as a Spring Boot API on Java 21 with Maven. Start on the latest supported Spring Boot 3.5.x line for initial scaffolding, then evaluate Spring Boot 4.x in a later ADR when ecosystem compatibility and project needs justify the upgrade.

The backend will expose explicit public HTTP APIs only. Every public endpoint should have validation, structured errors, OpenAPI documentation, observable behavior, and an intentional CORS policy. The backend will be containerized from day one.

## Consequences

- Java 21 provides a stable LTS runtime for the backend.
- Spring Boot 3.5.x is a conservative starting point while Spring Boot 4.x adoption matures.
- The project avoids mixing frontend server behavior with backend system-of-record behavior.
- Future upgrades to Spring Boot 4.x should be deliberate and tested rather than incidental.
