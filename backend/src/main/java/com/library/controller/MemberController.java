package com.library.controller;

import com.library.dto.request.MemberRequest;
import com.library.dto.response.MemberResponse;
import com.library.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * MemberController - Admin-only member management endpoints.
 * All operations require ADMIN role.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Members", description = "Library member management (Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class MemberController {

    private final MemberService memberService;

    /** GET /api/members?page=0&size=10 */
    @GetMapping
    @Operation(summary = "Get all members (paginated)")
    public ResponseEntity<Page<MemberResponse>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(memberService.getAllMembers(page, size));
    }

    /** GET /api/members/{id} */
    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    /** POST /api/members */
    @PostMapping
    @Operation(summary = "Create a new member")
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(request));
    }

    /** PUT /api/members/{id} */
    @PutMapping("/{id}")
    @Operation(summary = "Update member details")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    /** DELETE /api/members/{id} */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a member")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    /** PATCH /api/members/{id}/toggle-status */
    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle member ACTIVE/INACTIVE status")
    public ResponseEntity<MemberResponse> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.toggleStatus(id));
    }
}
