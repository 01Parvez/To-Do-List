package com.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * LoginRequest DTO - carries login credentials from client to server.
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
