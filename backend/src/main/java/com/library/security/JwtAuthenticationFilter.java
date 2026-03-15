package com.library.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter - intercepts every HTTP request to validate JWT
 * tokens.
 *
 * How it works:
 * 1. Extract JWT from "Authorization: Bearer <token>" header
 * 2. Validate the token (signature + expiry)
 * 3. Load the user from DB
 * 4. Set authentication in Spring Security's SecurityContext
 * 5. Continue with the request (FilterChain.doFilter)
 *
 * OncePerRequestFilter ensures this runs exactly once per request.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // Step 1: Extract JWT token from the Authorization header
            String jwt = extractTokenFromRequest(request);

            // Step 2: If token exists and is valid, authenticate the user
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

                // Step 3: Get username from token payload
                String username = jwtTokenProvider.getUsernameFromToken(jwt);

                // Step 4: Load user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Step 5: Create authentication object and set in SecurityContext
                // This is what Spring Security checks when evaluating @PreAuthorize etc.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // no credentials needed (already validated)
                        userDetails.getAuthorities() // roles/permissions
                );

                // Attach request details (IP, session) to the authentication
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Put authentication in SecurityContext so Spring Security knows user is logged
                // in
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Cannot set user authentication: {}", ex.getMessage());
        }

        // Always continue the filter chain - even if authentication fails
        // SecurityConfig handles the "access denied" for protected endpoints
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT from the Authorization header.
     * Expected format: "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
     *
     * @return token string, or null if not present/invalid format
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Check header exists and starts with "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix (7 chars)
        }
        return null;
    }
}
