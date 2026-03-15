package com.library.dto.response;

import com.library.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * MemberResponse DTO - sent to client when returning member data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDate membershipDate;
    private Member.MemberStatus status;

    /** Username of the linked user account */
    private String username;

    private Long userId;
    private LocalDateTime createdAt;
}
