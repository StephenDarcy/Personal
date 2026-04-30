package com.stephendarcy.personal.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ApiErrorFactory {

    public ApiErrorResponse create(
        HttpServletRequest request,
        HttpStatus status,
        String type,
        String title,
        String detail
    ) {
        return create(request, status, type, title, detail, List.of());
    }

    public ApiErrorResponse create(
        HttpServletRequest request,
        HttpStatus status,
        String type,
        String title,
        String detail,
        List<FieldErrorDetail> errors
    ) {
        return new ApiErrorResponse(
            type,
            title,
            status.value(),
            detail,
            request.getRequestURI(),
            CorrelationIds.current(request),
            errors
        );
    }
}
