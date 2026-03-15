package com.library.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Role entity - represents user roles in the system.
 * Roles: ADMIN (librarian) and USER (member)
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Role name - stored as enum string (e.g., "ROLE_ADMIN", "ROLE_USER")
     * @Enumerated(STRING) stores enum as its name, not ordinal index
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)
    private RoleName name;

    /**
     * Enum defining available role names.
     * Spring Security expects roles prefixed with "ROLE_"
     */
    public enum RoleName {
        ROLE_ADMIN,
        ROLE_USER
    }
}
