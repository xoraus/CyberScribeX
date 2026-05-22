package com.xoraus.cyberscribex.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xoraus.cyberscribex.payload.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        log.warn("[{}] Unauthorized access attempt to {}: {}", traceId, request.getRequestURI(), authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("X-Trace-Id", traceId);

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Authentication required. Please provide a valid JWT token.")
                .path(request.getRequestURI())
                .traceId(traceId)
                .timestamp(Instant.now())
                .build();

        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
