package com.library.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JwtTokenProvider - handles JWT token creation and validation.
 *
 * JWT (JSON Web Token) structure:
 * HEADER.PAYLOAD.SIGNATURE
 *
 * - Header: Algorithm type (HS256)
 * - Payload: Claims (username, expiry, issued-at)
 * - Signature: HMAC-SHA256 of header + payload, signed with secret key
 *
 * The client stores this token and sends it in every request:
 * Authorization: Bearer <token>
 */
@Component
@Slf4j
public class JwtTokenProvider {

    /**
     * Secret key from application.properties.
     * Injected via @Value - avoids hardcoding secrets in code.
     */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /** Token validity duration in milliseconds */
    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Generate a JWT token from an authenticated user.
     *
     * @param authentication - Spring Security authentication object (from login)
     * @return JWT token string
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                // Subject = username (stored in JWT payload)
                .setSubject(userPrincipal.getUsername())
                // When the token was issued
                .setIssuedAt(new Date())
                // When the token expires
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                // Sign with HMAC-SHA256 using our secret key
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract the username from a JWT token.
     * Parses the token and reads the "sub" (subject) claim.
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validate a JWT token:
     * 1. Check signature is valid (not tampered)
     * 2. Check token is not expired
     *
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Build the HMAC signing key from our secret string.
     * Decodes Base64-encoded secret and creates an HMAC-SHA key.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(jwtSecret.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
