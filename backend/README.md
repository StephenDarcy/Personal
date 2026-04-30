# Backend

This directory contains the public Spring Boot API.

## Ownership

- Framework: Spring Boot 3.5.x.
- Runtime: Java 21.
- Build: Maven.
- Packaging: executable Spring Boot jar. Container packaging should be added with the matching container scan gate before a Dockerfile is introduced.

## Boundaries

- Keep the backend as the system of record for server-side behavior.
- Publish OpenAPI documentation for public endpoints.
- Add explicit validation, structured errors, request correlation, and narrow CORS rules with the first API slice.
- Do not store durable visitor-submitted data or add private authenticated areas without an ADR and threat-model update.
- Keep sample configuration obviously fake and free of real secrets, credentials, and account IDs.

## Runtime Status API

The first backend slice implements the public runtime status boundary from [ADR 0009](../docs/adr/0009-first-backend-vertical-slice.md):

- `GET /api/v1/health`
- `GET /api/v1/version`
- structured public-safe errors
- request correlation through `X-Correlation-ID`
- narrow CORS for the intended local frontend origin

Both endpoints are read-only and accept no request body, query parameters, or path parameters.

Do not add product data, persistence, authentication, contact workflows, analytics collection, or other visitor-submitted data in this slice.

## Local Verification

From the repository root:

```powershell
cd backend
mvn --batch-mode verify
```

Run the API locally:

```powershell
cd backend
mvn --batch-mode spring-boot:run
```

Build a local container image with Spring Boot buildpacks:

```powershell
cd backend
mvn --batch-mode spring-boot:build-image -Dspring-boot.build-image.imageName=personal-api:local
```

The image command requires a local Docker-compatible runtime. Do not add a Dockerfile until container scanning is wired into the repository security gates.

Example local checks:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/health" -Headers @{ "X-Correlation-ID" = "req_20260115_103000_demo" }
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/version"
```
