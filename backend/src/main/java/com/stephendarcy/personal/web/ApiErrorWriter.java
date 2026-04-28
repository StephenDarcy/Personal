package com.stephendarcy.personal.web;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ApiErrorWriter {

    private final ApiErrorFactory errorFactory;
    private final ObjectMapper objectMapper;

    public ApiErrorWriter(ApiErrorFactory errorFactory, ObjectMapper objectMapper) {
        this.errorFactory = errorFactory;
        this.objectMapper = objectMapper;
    }

    public void write(
        HttpServletRequest request,
        HttpServletResponse response,
        HttpStatus status,
        String type,
        String title,
        String detail
    ) throws IOException {
        write(request, response, status, type, title, detail, List.of());
    }

    public void write(
        HttpServletRequest request,
        HttpServletResponse response,
        HttpStatus status,
        String type,
        String title,
        String detail,
        List<FieldErrorDetail> errors
    ) throws IOException {
        String correlationId = CorrelationIds.current(request);
        response.setStatus(status.value());
        response.setHeader(CorrelationIds.HEADER, correlationId);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
            response.getOutputStream(),
            errorFactory.create(request, status, type, title, detail, errors)
        );
    }
}
