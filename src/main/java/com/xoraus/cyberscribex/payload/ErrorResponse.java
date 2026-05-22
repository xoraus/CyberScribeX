package com.xoraus.cyberscribex.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standardized error response")
public class ErrorResponse {

    @Schema(description = "HTTP status code")
    private int status;

    @Schema(description = "Error type")
    private String error;

    @Schema(description = "Detailed error message")
    private String message;

    @Schema(description = "Request path")
    private String path;

    @Schema(description = "Timestamp of the error")
    @Builder.Default
    private Instant timestamp = Instant.now();

    @Schema(description = "Validation field errors")
    private Map<String, String> validationErrors;

    @Schema(description = "Trace ID for debugging")
    private String traceId;
}
