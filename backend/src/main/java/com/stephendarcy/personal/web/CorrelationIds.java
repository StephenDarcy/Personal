package com.stephendarcy.personal.web;

import java.util.UUID;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

public final class CorrelationIds {

    public static final String HEADER = "X-Correlation-ID";
    public static final String ATTRIBUTE = CorrelationIds.class.getName() + ".value";
    public static final String VALIDATION_MESSAGE =
        "Use 8 to 128 characters from letters, numbers, dot, underscore, colon, or hyphen.";

    private static final Pattern ACCEPTED = Pattern.compile("^[A-Za-z0-9._:-]{8,128}$");

    private CorrelationIds() {
    }

    public static boolean isValid(String value) {
        return value != null && ACCEPTED.matcher(value).matches();
    }

    public static String generate() {
        return "req_" + UUID.randomUUID();
    }

    public static String current(HttpServletRequest request) {
        Object value = request.getAttribute(ATTRIBUTE);
        if (value instanceof String correlationId && isValid(correlationId)) {
            return correlationId;
        }
        return generate();
    }
}
