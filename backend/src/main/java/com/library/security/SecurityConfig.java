package com.library.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * SecurityConfig - main Spring Security configuration.
 *
 * Configures:
 * 1. Which endpoints are public vs protected
 * 2. JWT-based stateless session (no cookies/server sessions)
 * 3. CORS (allows React frontend to call this API)
 * 4. BCrypt password encoding
 * 5. HTTP security rules
 *
 * @EnableMethodSecurity enables @PreAuthorize annotations on controller methods
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Main security filter chain - defines all HTTP security rules.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ── CSRF: Disabled for REST APIs ──
                // REST APIs use JWT tokens, not browser cookies, so CSRF is not applicable
                .csrf(csrf -> csrf.disable())

                // ── CORS: Enable cross-origin requests from React ──
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ── Session Management: STATELESS ──
                // JWT is stateless - no server-side sessions.
                // Each request must carry the JWT token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ── Authorization Rules ──
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no token needed
                        .requestMatchers("/api/auth/**").permitAll() // login, register
                        .requestMatchers("/swagger-ui/**", "/api-docs/**",
                                "/swagger-ui.html")
                        .permitAll() // API docs
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll() // Browse books (no login)

                        // Admin-only endpoints
                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
                        .requestMatchers("/api/members/**").hasRole("ADMIN")
                        .requestMatchers("/api/dashboard/admin/**").hasRole("ADMIN")

                        // Authenticated user endpoints
                        .requestMatchers("/api/borrow/**").authenticated()
                        .requestMatchers("/api/dashboard/**").authenticated()

                        // Everything else requires authentication
                        .anyRequest().authenticated())

                // ── Authentication Provider ──
                .authenticationProvider(authenticationProvider())

                // ── JWT Filter ──
                // Insert our JWT filter BEFORE the standard username/password filter.
                // This means JWT validation runs before Spring Security's own auth check.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS Configuration - allows React frontend (localhost:5173) to call this API.
     * In production, replace localhost:5173 with your actual domain.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from React dev server and production
        configuration.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://localhost:3000", "*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Authentication Provider - connects UserDetailsService and PasswordEncoder.
     * DaoAuthenticationProvider:
     * 1. Calls loadUserByUsername() to fetch user from DB
     * 2. Uses BCrypt to verify the submitted password against the stored hash
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * AuthenticationManager - used in AuthService to trigger authentication.
     * Spring Boot auto-configures this; we just expose it as a Bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCrypt Password Encoder - industry standard for password hashing.
     * BCrypt automatically handles salting and is resistant to rainbow table
     * attacks.
     * Cost factor 10 means 2^10 = 1024 hashing rounds (tunable for
     * security/performance).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
