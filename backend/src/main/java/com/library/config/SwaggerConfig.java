package com.library.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfig - configures SpringDoc / Swagger UI documentation.
 *
 * Access the interactive API docs at: http://localhost:8080/swagger-ui.html
 *
 * The SecurityScheme annotation adds a "Authorize" button in Swagger UI,
 * allowing you to enter your JWT token to test protected endpoints.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Library Management System API", version = "1.0", description = "RESTful API for managing books, members, and borrowing", contact = @Contact(name = "Library Admin", email = "admin@library.com")))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "Enter your JWT token (without 'Bearer ' prefix)")
public class SwaggerConfig {
    // No additional beans needed - SpringDoc auto-configures from annotations
}
