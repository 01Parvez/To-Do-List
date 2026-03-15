package com.library.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User entity - represents a system user (librarian or member).
 * Linked to Spring Security for authentication.
 *
 * @Table unique constraints prevent duplicate username/email
 */
@Entity
@Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    /**
     * Password stored as BCrypt hash - NEVER plain text.
     * The actual password hash is longer than 100 chars.
     */
    @Column(nullable = false, length = 120)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    /**
     * Many users can have many roles.
     * EAGER fetch: roles are always loaded with the user (needed for Security).
     * CascadeType.MERGE: role changes propagate to join table.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /** Automatically set when record is created */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Automatically updated on every save */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
