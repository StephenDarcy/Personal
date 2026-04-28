# ADR 0009: First Backend Vertical Slice

## Status

Accepted

## Context

The first backend implementation should be small, public-safe, and contract-led. It needs to prove the API boundary before adding product data, persistence, authentication, or Spring Boot-specific implementation details.

Accepted decisions already require a Spring Boot API, OpenAPI ownership, explicit validation, structured errors, observable behavior, narrow CORS rules, and no durable visitor-submitted data in v1.

## Decision

The first backend API capability will be a public runtime status slice.

This slice belongs in v1 because it proves that the backend can be built, tested, documented, containerized, observed, and called through the public API boundary without introducing visitor data, accounts, contact workflows, analytics collection, or other sensitive state.

The first implementation issue should define the contract before coding and stay limited to:

- `GET /api/v1/health`
- `GET /api/v1/version`
- A shared structured error response model
- Request correlation behavior
- OpenAPI publication and contract checks
- Narrow CORS configuration for the intended frontend origin

`GET /api/v1/health` should report whether the API process is ready to serve public traffic. It should not expose hostnames, internal dependency details, environment names, account identifiers, commit history, or raw exception messages.

`GET /api/v1/version` should report public-safe build metadata that helps verify deployments. The response may include fields such as service name, semantic version, build timestamp, and commit SHA when those values are intentionally prepared for public release. It must not include branch names, local paths, usernames, machine names, CI tokens, cloud account identifiers, or dependency inventories.

Both endpoints are read-only. They accept no request body, no query parameters, and no path parameters. Unexpected request bodies, unsupported methods, unsupported media types, invalid headers, and unmapped routes should return structured errors.

Structured error responses should use a stable shape:

- `type`: stable machine-readable error category
- `title`: short human-readable summary
- `status`: HTTP status code
- `detail`: public-safe explanation
- `instance`: request path or request instance identifier
- `correlationId`: request correlation identifier
- `errors`: optional field-level validation details when validation applies

The OpenAPI contract must document both success and error responses, examples, required fields, status codes, response content types, and correlation headers. Contract examples must use obviously fake, public-safe values.

Validation expectations for this first slice are intentionally narrow:

- Reject request bodies where none are allowed.
- Reject unsupported content types and methods.
- Keep response fields non-null where the contract marks them required.
- Keep public-safe formatting for version and timestamp fields.

## Consequences

- The first backend PR remains small and testable while still exercising production-shaped API behavior.
- The frontend can rely on generated contracts before consuming backend behavior.
- Product content, contact forms, analytics, persistence, and authentication remain out of scope until a later ADR approves their data and access boundaries.
- Health and version endpoints become the baseline for backend CI, container smoke checks, OpenAPI drift checks, CORS verification, and observability evidence.
