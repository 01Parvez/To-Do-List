package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthResponse DTO - returned to client after successful login.
 * Contains the JWT token and user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** JWT Bearer token - client must send this in Authorization header */
    private String token;

    /** Token type, always "Bearer" */
    @Builder.Default
    private String tokenType = "Bearer";

    private Long userId;
    private String username;
    private String email;

    /** User's role: "ROLE_ADMIN" or "ROLE_USER" */
    private String role;
}
