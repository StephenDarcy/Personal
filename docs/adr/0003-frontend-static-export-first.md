# ADR 0003: Frontend Static Export First

## Status

Accepted

## Context

The public website should be fast, low-cost, and simple to operate. The current threat model assumes static or prerendered frontend hosting by default. Next.js supports static export, but dynamic server-only features and default image optimization require additional runtime capabilities.

## Decision

Build the frontend with Next.js App Router, TypeScript, strict checking, and static export as the default production mode.

The frontend should avoid server-only behavior for v1 unless a future ADR justifies it. API reads should go to the external backend API through generated clients. Images should use a static-export-compatible approach, such as local static assets, an approved image provider, or explicit unoptimized handling when appropriate.

## Consequences

- The first public launch can use inexpensive static hosting and CDN caching.
- Frontend deployment has a smaller runtime attack surface.
- Server Actions, request-dependent Route Handlers, default Next.js image optimization, and other server runtime features are unavailable unless the decision is revisited.
- Dynamic behavior must be implemented either client-side against the backend API or through a later platform decision.
