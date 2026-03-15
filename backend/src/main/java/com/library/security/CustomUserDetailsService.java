package com.library.security;

import com.library.entity.User;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * CustomUserDetailsService - bridges our User entity with Spring Security.
 *
 * Spring Security's authentication requires a UserDetailsService that
 * loads user data from a data source. This implementation loads from MySQL.
 *
 * Called automatically by Spring Security during login to:
 * 1. Load user by username
 * 2. Return a UserDetails object with password and roles
 * 3. Spring Security compares submitted password with BCrypt hash
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by username - REQUIRED by UserDetailsService interface.
     * 
     * @Transactional ensures roles are loaded within the same DB session.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user in database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));

        // Convert our Role entities to Spring Security GrantedAuthority objects
        // Spring Security uses GrantedAuthority to check permissions
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());

        // Return Spring Security's UserDetails with username, BCrypt password, and
        // roles
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // BCrypt hash - Spring Security will verify this
                user.isEnabled(), // account active?
                true, // account non-expired
                true, // credentials non-expired
                true, // account non-locked
                authorities);
    }
}
