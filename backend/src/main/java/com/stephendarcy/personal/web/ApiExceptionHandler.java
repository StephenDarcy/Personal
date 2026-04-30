package com.stephendarcy.personal.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final ApiErrorFactory errorFactory;

    public ApiExceptionHandler(ApiErrorFactory errorFactory) {
        this.errorFactory = errorFactory;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    ResponseEntity<ApiErrorResponse> handleNoHandler(NoHandlerFoundException ex, HttpServletRequest request) {
        return error(
            request,
            HttpStatus.NOT_FOUND,
            "request.not-found",
            "Route not found",
            "No public API route matched the request path."
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiErrorResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        return error(
            request,
            HttpStatus.NOT_FOUND,
            "request.not-found",
            "Route not found",
            "No public API route matched the request path."
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiErrorResponse> handleMethodNotAllowed(
        HttpRequestMethodNotSupportedException ex,
        HttpServletRequest request
    ) {
        HttpHeaders headers = errorHeaders(request);
        if (ex.getSupportedHttpMethods() != null) {
            headers.setAllow(ex.getSupportedHttpMethods());
        }
        return error(
            request,
            headers,
            HttpStatus.METHOD_NOT_ALLOWED,
            "request.method-not-allowed",
            "Method not allowed",
            "The requested method is not supported for this public API route."
        );
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    ResponseEntity<ApiErrorResponse> handleNotAcceptable(
        HttpMediaTypeNotAcceptableException ex,
        HttpServletRequest request
    ) {
        return error(
            request,
            HttpStatus.NOT_ACCEPTABLE,
            "request.not-acceptable",
            "Response format not supported",
            "This endpoint returns application/json responses."
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ApiErrorResponse> handleUnsupportedMediaType(
        HttpMediaTypeNotSupportedException ex,
        HttpServletRequest request
    ) {
        return error(
            request,
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "request.unsupported-media-type",
            "Request content type not supported",
            "This read-only endpoint does not accept a request body."
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(this::toFieldError)
            .toList();
        return error(
            request,
            HttpStatus.BAD_REQUEST,
            "request.validation-failed",
            "Request validation failed",
            "The request did not satisfy the public API contract.",
            fieldErrors
        );
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        LOGGER.error(
            "Unexpected API request failure. correlationId={} path={}",
            CorrelationIds.current(request),
            request.getRequestURI(),
            ex
        );
        return error(
            request,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "runtime.internal-error",
            "Request could not be completed",
            "The server could not complete the request."
        );
    }

    private FieldErrorDetail toFieldError(FieldError error) {
        return new FieldErrorDetail(error.getField(), "The supplied value is not accepted.");
    }

    private ResponseEntity<ApiErrorResponse> error(
        HttpServletRequest request,
        HttpStatus status,
        String type,
        String title,
        String detail
    ) {
        return error(request, status, type, title, detail, List.of());
    }

    private ResponseEntity<ApiErrorResponse> error(
        HttpServletRequest request,
        HttpStatus status,
        String type,
        String title,
        String detail,
        List<FieldErrorDetail> errors
    ) {
        return error(request, errorHeaders(request), status, type, title, detail, errors);
    }

    private ResponseEntity<ApiErrorResponse> error(
        HttpServletRequest request,
        HttpHeaders headers,
        HttpStatus status,
        String type,
        String title,
        String detail
    ) {
        return error(request, headers, status, type, title, detail, List.of());
    }

    private ResponseEntity<ApiErrorResponse> error(
        HttpServletRequest request,
        HttpHeaders headers,
        HttpStatus status,
        String type,
        String title,
        String detail,
        List<FieldErrorDetail> errors
    ) {
        return new ResponseEntity<>(
            errorFactory.create(request, status, type, title, detail, errors),
            headers,
            status
        );
    }

    private HttpHeaders errorHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(CorrelationIds.HEADER, CorrelationIds.current(request));
        return headers;
    }
}
