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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class CorrelationFilter extends OncePerRequestFilter {

    private final ApiErrorWriter errorWriter;

    public CorrelationFilter(ApiErrorWriter errorWriter) {
        this.errorWriter = errorWriter;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        List<String> suppliedValues = Collections.list(request.getHeaders(CorrelationIds.HEADER));
        if (suppliedValues.isEmpty()) {
            setCorrelationId(request, response, CorrelationIds.generate());
            filterChain.doFilter(request, response);
            return;
        }

        if (suppliedValues.size() != 1 || !CorrelationIds.isValid(suppliedValues.getFirst())) {
            setCorrelationId(request, response, CorrelationIds.generate());
            errorWriter.write(
                request,
                response,
                HttpStatus.BAD_REQUEST,
                "request.invalid-header",
                "Invalid request header",
                "The supplied correlation identifier is not in an accepted format.",
                List.of(new FieldErrorDetail(CorrelationIds.HEADER, CorrelationIds.VALIDATION_MESSAGE))
            );
            return;
        }

        setCorrelationId(request, response, suppliedValues.getFirst());
        filterChain.doFilter(request, response);
    }

    private void setCorrelationId(HttpServletRequest request, HttpServletResponse response, String correlationId) {
        request.setAttribute(CorrelationIds.ATTRIBUTE, correlationId);
        response.setHeader(CorrelationIds.HEADER, correlationId);
    }
}
