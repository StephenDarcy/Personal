package com.stephendarcy.personal.runtime;

import java.time.Instant;

public record HealthResponse(
    String status,
    String serviceName,
    Instant checkedAt
) {
}
