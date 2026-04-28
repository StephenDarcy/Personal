package com.stephendarcy.personal.web;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RuntimeStatusRequestValidationFilter extends OncePerRequestFilter {

    private final ApiErrorWriter errorWriter;

    public RuntimeStatusRequestValidationFilter(ApiErrorWriter errorWriter) {
        this.errorWriter = errorWriter;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (!isRuntimeStatusPath(request) || HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!acceptsJson(request)) {
            errorWriter.write(
                request,
                response,
                HttpStatus.NOT_ACCEPTABLE,
                "request.not-acceptable",
                "Response format not supported",
                "This endpoint returns application/json responses."
            );
            return;
        }

        if (!HttpMethod.GET.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getQueryString() != null) {
            errorWriter.write(
                request,
                response,
                HttpStatus.BAD_REQUEST,
                "request.validation-failed",
                "Request validation failed",
                "This read-only endpoint does not accept query parameters.",
                List.of(new FieldErrorDetail("query", "Remove query parameters from this request."))
            );
            return;
        }

        if (hasRequestBodyMetadata(request)) {
            errorWriter.write(
                request,
                response,
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "request.unsupported-media-type",
                "Request content type not supported",
                "This read-only endpoint does not accept a request body."
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRuntimeStatusPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "/api/v1/health".equals(path) || "/api/v1/version".equals(path);
    }

    private boolean acceptsJson(HttpServletRequest request) {
        List<String> acceptHeaders = Collections.list(request.getHeaders(HttpHeaders.ACCEPT));
        if (acceptHeaders.isEmpty()) {
            return true;
        }

        try {
            List<MediaType> requestedTypes = MediaType.parseMediaTypes(acceptHeaders);
            return requestedTypes.stream().anyMatch((requestedType) ->
                requestedType.includes(MediaType.ALL) || requestedType.isCompatibleWith(MediaType.APPLICATION_JSON)
            );
        }
        catch (InvalidMediaTypeException ex) {
            return false;
        }
    }

    private boolean hasRequestBodyMetadata(HttpServletRequest request) {
        return request.getContentLengthLong() > 0
            || request.getHeader(HttpHeaders.TRANSFER_ENCODING) != null
            || request.getHeader(HttpHeaders.CONTENT_TYPE) != null;
    }
}
