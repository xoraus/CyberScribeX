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
@Schema(description = "Standardized API response wrapper")
public class ApiResult<T> {

    @Schema(description = "Indicates if the request was successful")
    private boolean success;

    @Schema(description = "Human-readable message")
    private String message;

    @Schema(description = "Response payload")
    private T data;

    @Schema(description = "Timestamp of the response")
    @Builder.Default
    private Instant timestamp = Instant.now();

    @Schema(description = "Validation errors if any")
    private Map<String, String> errors;

    @Schema(description = "Request path")
    private String path;

    public static <T> ApiResult<T> success(T data) {
        return ApiResult.<T>builder()
                .success(true)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return ApiResult.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResult<T> error(String message) {
        return ApiResult.<T>builder()
                .success(false)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResult<T> error(String message, Map<String, String> errors) {
        return ApiResult.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .timestamp(Instant.now())
                .build();
    }
}
