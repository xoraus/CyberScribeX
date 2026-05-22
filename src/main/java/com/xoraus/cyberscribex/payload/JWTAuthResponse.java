package com.xoraus.cyberscribex.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT Authentication Response")
public class JWTAuthResponse {

    @Schema(description = "Type of token", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Access token for API authentication")
    private String accessToken;

    @Schema(description = "Refresh token for obtaining new access tokens")
    private String refreshToken;

    @Schema(description = "Token expiration time in milliseconds", example = "900000")
    private Long expiresIn;

    @Schema(description = "Username of the authenticated user", example = "johndoe")
    private String username;

    @Schema(description = "Roles assigned to the user", example = "[\"ROLE_USER\"]")
    private java.util.Set<String> roles;
}
