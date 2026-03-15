package com.library.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Member entity - represents a library member (physical person with membership).
 *
 * A Member is linked to a User account (for login).
 * One User can have one Member profile.
 */
@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 15)
    private String phone;

    @Column(name = "membership_date")
    private LocalDate membershipDate;

    /**
     * Membership status: ACTIVE or INACTIVE.
     * Inactive members cannot borrow books.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private MemberStatus status = MemberStatus.ACTIVE;

    /**
     * One-to-One relationship: each member has exactly one user account.
     * @JoinColumn: member table stores the foreign key user_id.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Enum for member account status */
    public enum MemberStatus {
        ACTIVE,
        INACTIVE
    }
}
