package com.stephendarcy.personal.runtime;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.stephendarcy.personal.web.CorrelationIds;

@SpringBootTest
@AutoConfigureMockMvc
class RuntimeStatusApiTests {

    private static final String CORRELATION_PATTERN = "^[A-Za-z0-9._:-]{8,128}$";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthReturnsContractAlignedReadinessResponse() throws Exception {
        mockMvc.perform(get("/api/v1/health").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string(CorrelationIds.HEADER, matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.status").value("ready"))
            .andExpect(jsonPath("$.serviceName").value("personal-api"))
            .andExpect(jsonPath("$.checkedAt").isString());
    }

    @Test
    void versionReturnsPublicSafeBuildMetadata() throws Exception {
        mockMvc.perform(get("/api/v1/version").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string(CorrelationIds.HEADER, matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.serviceName").value("personal-api"))
            .andExpect(jsonPath("$.version").value("0.1.0"))
            .andExpect(jsonPath("$.buildTimestamp").value("2026-01-15T10:00:00Z"))
            .andExpect(jsonPath("$.commitSha").value("0123456789abcdef0123456789abcdef01234567"));
    }

    @Test
    void validCorrelationIdIsEchoedInSuccessHeader() throws Exception {
        String correlationId = "req_20260115_103000_demo";

        mockMvc.perform(get("/api/v1/health").header(CorrelationIds.HEADER, correlationId))
            .andExpect(status().isOk())
            .andExpect(header().string(CorrelationIds.HEADER, correlationId));
    }

    @Test
    void invalidCorrelationIdReturnsStructuredErrorWithoutEchoingUnsafeInput() throws Exception {
        mockMvc.perform(get("/api/v1/health").header(CorrelationIds.HEADER, "bad value with spaces"))
            .andExpect(status().isBadRequest())
            .andExpect(header().string(CorrelationIds.HEADER, matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.type").value("request.invalid-header"))
            .andExpect(jsonPath("$.title").value("Invalid request header"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("The supplied correlation identifier is not in an accepted format."))
            .andExpect(jsonPath("$.instance").value("/api/v1/health"))
            .andExpect(jsonPath("$.correlationId").value(matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.correlationId").value(org.hamcrest.Matchers.not("bad value with spaces")))
            .andExpect(jsonPath("$.errors[0].field").value(CorrelationIds.HEADER));
    }

    @Test
    void unsupportedMethodReturnsStructuredError() throws Exception {
        mockMvc.perform(post("/api/v1/health").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(header().string(HttpHeaders.ALLOW, org.hamcrest.Matchers.containsString("GET")))
            .andExpect(header().string(CorrelationIds.HEADER, matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.type").value("request.method-not-allowed"))
            .andExpect(jsonPath("$.status").value(405))
            .andExpect(jsonPath("$.instance").value("/api/v1/health"));
    }

    @Test
    void unsupportedMethodReturnsMethodErrorBeforeAcceptNegotiation() throws Exception {
        mockMvc.perform(post("/api/v1/health").accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(header().string(HttpHeaders.ALLOW, org.hamcrest.Matchers.containsString("GET")))
            .andExpect(jsonPath("$.type").value("request.method-not-allowed"))
            .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void unsupportedResponseFormatReturnsStructuredError() throws Exception {
        mockMvc.perform(get("/api/v1/version").accept(MediaType.APPLICATION_XML))
            .andExpect(status().isNotAcceptable())
            .andExpect(header().string(CorrelationIds.HEADER, matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.type").value("request.not-acceptable"))
            .andExpect(jsonPath("$.status").value(406))
            .andExpect(jsonPath("$.instance").value("/api/v1/version"));
    }

    @Test
    void jsonWithZeroQualityReturnsStructuredNotAcceptableError() throws Exception {
        mockMvc.perform(get("/api/v1/version").header(HttpHeaders.ACCEPT, "application/json;q=0"))
            .andExpect(status().isNotAcceptable())
            .andExpect(header().string(CorrelationIds.HEADER, matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.type").value("request.not-acceptable"))
            .andExpect(jsonPath("$.status").value(406));
    }

    @Test
    void explicitJsonExclusionWinsOverWildcardAcceptHeader() throws Exception {
        mockMvc.perform(get("/api/v1/version").header(HttpHeaders.ACCEPT, "application/json;q=0, */*;q=0.8"))
            .andExpect(status().isNotAcceptable())
            .andExpect(header().string(CorrelationIds.HEADER, matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.type").value("request.not-acceptable"))
            .andExpect(jsonPath("$.status").value(406));
    }

    @Test
    void unexpectedContentTypeReturnsStructuredUnsupportedMediaTypeError() throws Exception {
        mockMvc.perform(get("/api/v1/health").contentType(MediaType.TEXT_PLAIN))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(header().string(CorrelationIds.HEADER, matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.type").value("request.unsupported-media-type"))
            .andExpect(jsonPath("$.status").value(415))
            .andExpect(jsonPath("$.instance").value("/api/v1/health"));
    }

    @Test
    void unexpectedBodyReturnsStructuredUnsupportedMediaTypeError() throws Exception {
        mockMvc.perform(get("/api/v1/health")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"unexpected\":true}"))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(header().string(CorrelationIds.HEADER, matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.type").value("request.unsupported-media-type"))
            .andExpect(jsonPath("$.detail").value("This read-only endpoint does not accept a request body."));
    }

    @Test
    void queryParametersReturnStructuredValidationError() throws Exception {
        mockMvc.perform(get("/api/v1/health?detail=true"))
            .andExpect(status().isBadRequest())
            .andExpect(header().string(CorrelationIds.HEADER, matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.type").value("request.validation-failed"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.errors[0].field").value("query"));
    }

    @Test
    void runtimeRouteValidationAppliesWithServletContextPath() throws Exception {
        mockMvc.perform(get("/personal/api/v1/health?detail=true").contextPath("/personal"))
            .andExpect(status().isBadRequest())
            .andExpect(header().string(CorrelationIds.HEADER, matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.type").value("request.validation-failed"))
            .andExpect(jsonPath("$.instance").value("/personal/api/v1/health"));
    }

    @Test
    void unmappedRouteReturnsStructuredNotFoundError() throws Exception {
        mockMvc.perform(get("/api/v1/example").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(header().string(CorrelationIds.HEADER, matchesPattern(CORRELATION_PATTERN)))
            .andExpect(jsonPath("$.type").value("request.not-found"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.instance").value("/api/v1/example"));
    }

    @Test
    void allowedFrontendOriginReceivesCorsResponseHeaders() throws Exception {
        mockMvc.perform(options("/api/v1/health")
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, CorrelationIds.HEADER))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000"))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, org.hamcrest.Matchers.containsString("GET")))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, org.hamcrest.Matchers.containsString(CorrelationIds.HEADER)));
    }
}
