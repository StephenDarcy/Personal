package com.stephendarcy.personal.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(OutputCaptureExtension.class)
class ApiExceptionHandlerTests {

    private final ApiExceptionHandler handler = new ApiExceptionHandler(new ApiErrorFactory());

    @Test
    void unexpectedExceptionsAreLoggedBeforeSanitizedResponseIsReturned(CapturedOutput output) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/version");
        request.setAttribute(CorrelationIds.ATTRIBUTE, "req_20260115_103000_demo");

        ResponseEntity<ApiErrorResponse> response = handler.handleUnexpected(
            new IllegalStateException("example failure"),
            request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().detail()).isEqualTo("The server could not complete the request.");
        assertThat(output).contains("Unexpected API request failure");
        assertThat(output).contains("req_20260115_103000_demo");
        assertThat(output).contains("/api/v1/version");
    }
}
