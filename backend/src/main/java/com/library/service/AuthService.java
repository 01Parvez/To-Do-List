package com.library.service;

import com.library.dto.request.LoginRequest;
import com.library.dto.request.RegisterRequest;
import com.library.dto.response.AuthResponse;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.exception.DuplicateResourceException;
import com.library.repository.RoleRepository;
import com.library.repository.UserRepository;
import com.library.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * AuthService - handles user registration and login.
 *
 * Registration flow:
 * 1. Validate username and email uniqueness
 * 2. Encode password with BCrypt
 * 3. Assign default ROLE_USER
 * 4. Save to database
 *
 * Login flow:
 * 1. AuthenticationManager verifies credentials (BCrypt comparison)
 * 2. If valid, generate JWT token
 * 3. Return token + user info to client
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user with default ROLE_USER.
     *
     * @param request - contains username, email, password
     * @return AuthResponse with JWT token (auto-login after registration)
     */
    public AuthResponse register(RegisterRequest request) {
        // Check for duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + request.getUsername());
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        // Fetch the USER role from DB (must exist - created by DataInitializer)
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found. Run DataInitializer first."));

        // Build and save the user with BCrypt-hashed password
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt hash
                .roles(new HashSet<>(Set.of(userRole)))
                .enabled(true)
                .build();

        userRepository.save(user);
        log.info("Registered new user: {}", user.getUsername());

        // Auto-login: authenticate the new user and generate JWT
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        return buildAuthResponse(authentication, user);
    }

    /**
     * Authenticate user and return JWT token.
     *
     * @param request - username and password
     * @return AuthResponse with JWT token
     */
    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager calls CustomUserDetailsService.loadUserByUsername()
        // then compares BCrypt hash. Throws BadCredentialsException if wrong.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // Store authentication in SecurityContext for this request
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        log.info("User logged in: {}", user.getUsername());
        return buildAuthResponse(authentication, user);
    }

    /** Build the AuthResponse DTO from authentication and user entity */
    private AuthResponse buildAuthResponse(Authentication authentication, User user) {
        String token = jwtTokenProvider.generateToken(authentication);

        // Get the primary role (first authority)
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(role)
                .build();
    }
}
