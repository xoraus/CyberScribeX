package com.xoraus.cyberscribex.controller;

import com.xoraus.cyberscribex.payload.ApiResult;
import com.xoraus.cyberscribex.payload.JWTAuthResponse;
import com.xoraus.cyberscribex.payload.LoginDto;
import com.xoraus.cyberscribex.payload.RegisterDto;
import com.xoraus.cyberscribex.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "User Login",
            description = "Authenticate a user and return JWT tokens"
    )
    @ApiResponse(responseCode = "200", description = "Successfully authenticated")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "423", description = "Account locked")
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<ApiResult<JWTAuthResponse>> login(@Valid @RequestBody LoginDto loginDto) {
        JWTAuthResponse response = authService.login(loginDto);
        return ResponseEntity.ok(ApiResult.success("Login successful", response));
    }

    @Operation(
            summary = "User Registration",
            description = "Register a new user account"
    )
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "409", description = "Username or email already exists")
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<ApiResult<JWTAuthResponse>> register(@Valid @RequestBody RegisterDto registerDto) {
        JWTAuthResponse response = authService.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.success("User registered successfully", response));
    }

    @Operation(
            summary = "Refresh Access Token",
            description = "Obtain a new access token using a refresh token"
    )
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResult<JWTAuthResponse>> refreshToken(
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        JWTAuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResult.success("Token refreshed successfully", response));
    }
}
