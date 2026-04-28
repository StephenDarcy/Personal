package com.stephendarcy.personal.web;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiWebConfiguration implements WebMvcConfigurer {

    private final ApiCorsProperties corsProperties;

    public ApiWebConfiguration(ApiCorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(corsProperties.allowedOrigin())
            .allowedMethods("GET", "OPTIONS")
            .allowedHeaders(HttpHeaders.ACCEPT, HttpHeaders.CONTENT_TYPE, CorrelationIds.HEADER)
            .exposedHeaders(CorrelationIds.HEADER)
            .maxAge(300);
    }
}
