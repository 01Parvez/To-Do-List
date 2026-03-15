package com.library.config;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * DataInitializer - runs on application startup to seed initial data.
 *
 * Implements CommandLineRunner: Spring Boot calls run() after the app starts.
 * Checks if data already exists before inserting (idempotent - safe to run
 * multiple times).
 *
 * Seeds:
 * 1. ROLE_ADMIN and ROLE_USER roles
 * 2. Default admin user (admin@library.com / admin123)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Initializing database with default data ===");

        // Step 1: Create roles if they don't exist
        Role adminRole = createRoleIfNotExists(Role.RoleName.ROLE_ADMIN);
        Role userRole = createRoleIfNotExists(Role.RoleName.ROLE_USER);

        // Step 2: Create default ADMIN user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@library.com")
                    .password(passwordEncoder.encode("admin123")) // BCrypt hash
                    .enabled(true)
                    .roles(Set.of(adminRole, userRole)) // Admin has both roles
                    .build();
            userRepository.save(admin);
            log.info("Created default admin user: admin / admin123");
        }

        // Step 3: Create a demo regular user
        if (!userRepository.existsByUsername("user")) {
            User demoUser = User.builder()
                    .username("user")
                    .email("user@library.com")
                    .password(passwordEncoder.encode("user123"))
                    .enabled(true)
                    .roles(Set.of(userRole))
                    .build();
            userRepository.save(demoUser);
            log.info("Created demo user: user / user123");
        }

        log.info("=== Database initialization complete ===");
        log.info("Login credentials -> Admin: admin/admin123 | User: user/user123");
    }

    /**
     * Create a role only if it doesn't already exist.
     * Returns the existing or newly created role.
     */
    private Role createRoleIfNotExists(Role.RoleName roleName) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            Role saved = roleRepository.save(role);
            log.info("Created role: {}", roleName);
            return saved;
        });
    }
}
