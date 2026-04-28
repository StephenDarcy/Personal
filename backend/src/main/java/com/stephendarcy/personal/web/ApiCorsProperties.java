package com.stephendarcy.personal.web;

import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.cors")
public record ApiCorsProperties(
    @NotBlank String allowedOrigin
) {
}
