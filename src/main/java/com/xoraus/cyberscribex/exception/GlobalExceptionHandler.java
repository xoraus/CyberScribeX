package com.xoraus.cyberscribex.exception;

import com.xoraus.cyberscribex.payload.ApiResult;
import com.xoraus.cyberscribex.payload.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[{}] Resource not found: {}", traceId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header(TRACE_ID_HEADER, traceId)
                .body(error);
    }

    @ExceptionHandler(CyberScribeXAPIException.class)
    public ResponseEntity<ErrorResponse> handleCyberScribeXAPIException(
            CyberScribeXAPIException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[{}] API exception: {}", traceId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(ex.getHttpStatus().value())
                .error(ex.getHttpStatus().getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(ex.getHttpStatus())
                .header(TRACE_ID_HEADER, traceId)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResult<Void>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        String traceId = generateTraceId();
        log.warn("[{}] Validation failed: {}", traceId, errors);

        ApiResult<Void> response = ApiResult.<Void>builder()
                .success(false)
                .message("Validation failed")
                .errors(errors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header(TRACE_ID_HEADER, traceId)
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[{}] Constraint violation: {}", traceId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Constraint violation: " + ex.getMessage())
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header(TRACE_ID_HEADER, traceId)
                .body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[{}] Bad credentials: {}", traceId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Invalid username or password")
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(TRACE_ID_HEADER, traceId)
                .body(error);
    }

    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.LOCKED)
    public ResponseEntity<ErrorResponse> handleLockedException(
            LockedException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[{}] Account locked: {}", traceId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.LOCKED.value())
                .error(HttpStatus.LOCKED.getReasonPhrase())
                .message("Your account has been locked due to too many failed login attempts. Please try again later.")
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.LOCKED)
                .header(TRACE_ID_HEADER, traceId)
                .body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[{}] Access denied: {}", traceId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("You do not have permission to access this resource")
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .header(TRACE_ID_HEADER, traceId)
                .body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[{}] Data integrity violation: {}", traceId, ex.getMostSpecificCause().getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message("Data conflict occurred. The resource may already exist.")
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .header(TRACE_ID_HEADER, traceId)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Unexpected error: {}", traceId, ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred. Please contact support with trace ID: " + traceId)
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(TRACE_ID_HEADER, traceId)
                .body(error);
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
