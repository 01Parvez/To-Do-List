package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LibraryManagementApplication - Main entry point for the Spring Boot
 * application.
 *
 * @SpringBootApplication is a convenience annotation that adds:
 *                        - @Configuration: marks this as a configuration class
 *                        - @EnableAutoConfiguration: enables Spring Boot's
 *                        auto-configuration
 *                        - @ComponentScan: scans com.library package
 *                        for @Component, @Service, etc.
 *
 *                        To run: mvn spring-boot:run
 *                        Or: java -jar target/library-management-1.0.0.jar
 *
 *                        After starting:
 *                        - API: http://localhost:8080/api
 *                        - Swagger UI: http://localhost:8080/swagger-ui.html
 */
@SpringBootApplication
public class LibraryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }
}
