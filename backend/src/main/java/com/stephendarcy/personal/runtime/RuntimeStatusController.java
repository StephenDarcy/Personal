package com.stephendarcy.personal.runtime;

import java.time.Clock;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class RuntimeStatusController {

    private final Clock clock;
    private final RuntimeStatusProperties properties;

    public RuntimeStatusController(Clock clock, RuntimeStatusProperties properties) {
        this.clock = clock;
        this.properties = properties;
    }

    @GetMapping("/health")
    HealthResponse getHealth() {
        return new HealthResponse("ready", properties.serviceName(), clock.instant());
    }

    @GetMapping("/version")
    VersionResponse getVersion() {
        return new VersionResponse(
            properties.serviceName(),
            properties.version(),
            properties.buildTimestamp(),
            properties.commitSha()
        );
    }
}
