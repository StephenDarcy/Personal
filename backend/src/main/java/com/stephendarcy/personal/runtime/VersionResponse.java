package com.stephendarcy.personal.runtime;

import java.time.Instant;

public record VersionResponse(
    String serviceName,
    String version,
    Instant buildTimestamp,
    String commitSha
) {
}
