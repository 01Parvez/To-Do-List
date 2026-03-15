package com.library.repository;

import com.library.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RoleRepository - data access for Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by its name enum.
     * Used during registration to assign default role.
     */
    Optional<Role> findByName(Role.RoleName name);
}
