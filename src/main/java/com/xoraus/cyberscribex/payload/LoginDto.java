package com.xoraus.cyberscribex.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Login Data Transfer Object")
public class LoginDto {

    @Schema(description = "Username or email address", example = "johndoe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    @Schema(description = "Password", example = "SecureP@ss123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is required")
    private String password;
}
