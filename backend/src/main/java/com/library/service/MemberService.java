package com.library.service;

import com.library.dto.request.MemberRequest;
import com.library.dto.response.MemberResponse;
import com.library.entity.Member;
import com.library.entity.User;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.MemberRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * MemberService - business logic for library member management (Admin only).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    /** Get all members with pagination */
    @Transactional(readOnly = true)
    public Page<MemberResponse> getAllMembers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return memberRepository.findAll(pageable).map(this::mapToResponse);
    }

    /** Get a single member by ID */
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        return mapToResponse(member);
    }

    /** Create a new member, optionally linking to a User account */
    public MemberResponse createMember(MemberRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Member already exists with email: " + request.getEmail());
        }

        Member member = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .membershipDate(request.getMembershipDate() != null
                        ? request.getMembershipDate()
                        : LocalDate.now())
                .status(Member.MemberStatus.ACTIVE)
                .build();

        // Link to user account (optional)
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
            member.setUser(user);
        }

        Member saved = memberRepository.save(member);
        log.info("Created member: {} (id={})", saved.getName(), saved.getId());
        return mapToResponse(saved);
    }

    /** Update an existing member */
    public MemberResponse updateMember(Long id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));

        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        if (request.getMembershipDate() != null) {
            member.setMembershipDate(request.getMembershipDate());
        }

        Member updated = memberRepository.save(member);
        log.info("Updated member id={}", id);
        return mapToResponse(updated);
    }

    /** Delete a member by ID */
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        memberRepository.delete(member);
        log.info("Deleted member id={}", id);
    }

    /** Toggle member status ACTIVE ↔ INACTIVE */
    public MemberResponse toggleStatus(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        member.setStatus(member.getStatus() == Member.MemberStatus.ACTIVE
                ? Member.MemberStatus.INACTIVE
                : Member.MemberStatus.ACTIVE);
        return mapToResponse(memberRepository.save(member));
    }

    private MemberResponse mapToResponse(Member m) {
        return MemberResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .email(m.getEmail())
                .phone(m.getPhone())
                .membershipDate(m.getMembershipDate())
                .status(m.getStatus())
                .username(m.getUser() != null ? m.getUser().getUsername() : null)
                .userId(m.getUser() != null ? m.getUser().getId() : null)
                .createdAt(m.getCreatedAt())
                .build();
    }
}
