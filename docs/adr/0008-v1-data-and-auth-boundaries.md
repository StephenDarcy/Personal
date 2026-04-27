# ADR 0008: V1 Data and Auth Boundaries

## Status

Accepted

## Context

The first public version should minimize sensitive data handling. User accounts, private dashboards, contact-message storage, analytics identity, and administrative workflows would add privacy, security, and operational obligations.

## Decision

V1 will avoid user accounts, private authenticated product areas, and durable visitor-submitted data unless a later ADR approves a specific need.

The first backend API should expose only intentional public data and operational endpoints. Any future contact form, analytics event collection, admin workflow, or persistent data store requires an update to the threat model and a new or amended ADR before implementation.

## Consequences

- The initial launch has a smaller privacy and security footprint.
- The backend can still demonstrate validation, contracts, observability, and containerized operation through public-safe endpoints.
- Product ideas that require persistence or authentication must wait for explicit data handling and access-control decisions.
