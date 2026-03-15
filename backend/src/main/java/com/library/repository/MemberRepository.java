package com.library.repository;

import com.library.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MemberRepository - data access for Member entity.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    /** Find member linked to a specific user account */
    Optional<Member> findByUserId(Long userId);
}
