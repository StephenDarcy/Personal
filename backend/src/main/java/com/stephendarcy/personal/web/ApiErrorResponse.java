package com.stephendarcy.personal.web;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(
    String type,
    String title,
    int status,
    String detail,
    String instance,
    String correlationId,
    List<FieldErrorDetail> errors
) {
}
