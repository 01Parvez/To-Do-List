package com.library.repository;

import com.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - data access layer for User entity.
 * Spring Data JPA auto-implements all CRUD methods.
 * We only need to declare custom query methods here.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username - used during login and JWT validation.
     * Spring Data auto-generates SQL: SELECT * FROM users WHERE username = ?
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email - used to check for duplicate emails during registration.
     */
    Optional<User> findByEmail(String email);

    /** Check if username already exists */
    boolean existsByUsername(String username);

    /** Check if email already exists */
    boolean existsByEmail(String email);
}
