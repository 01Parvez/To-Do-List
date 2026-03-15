package com.library.controller;

import com.library.dto.request.BorrowRequest;
import com.library.dto.response.BorrowResponse;
import com.library.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BorrowController - handles book borrowing and returning.
 *
 * All endpoints require authentication.
 * The username is extracted from the JWT via Authentication object.
 */
@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
@Tag(name = "Borrowing", description = "Book borrowing and returning")
@SecurityRequirement(name = "bearerAuth")
public class BorrowController {

    private final BorrowService borrowService;

    /**
     * POST /api/borrow
     * Borrow a book. The user is determined from JWT token.
     * Body: { "bookId": 5 }
     */
    @PostMapping
    @Operation(summary = "Borrow a book")
    public ResponseEntity<BorrowResponse> borrowBook(
            @Valid @RequestBody BorrowRequest request,
            Authentication authentication) {
        // authentication.getName() returns the username from JWT
        String username = authentication.getName();
        return ResponseEntity.ok(borrowService.borrowBook(username, request));
    }

    /**
     * POST /api/borrow/return/{borrowId}
     * Return a borrowed book. Calculates fine if overdue.
     */
    @PostMapping("/return/{borrowId}")
    @Operation(summary = "Return a borrowed book")
    public ResponseEntity<BorrowResponse> returnBook(@PathVariable Long borrowId) {
        return ResponseEntity.ok(borrowService.returnBook(borrowId));
    }

    /**
     * GET /api/borrow/my-borrows
     * Get active borrows for the logged-in user.
     */
    @GetMapping("/my-borrows")
    @Operation(summary = "Get current user's active borrows")
    public ResponseEntity<List<BorrowResponse>> getMyActiveBorrows(Authentication authentication) {
        return ResponseEntity.ok(borrowService.getActiveBorrows(authentication.getName()));
    }

    /**
     * GET /api/borrow/user/{userId}?page=0&size=10
     * Get paginated borrow history for a user (admin or self).
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get borrow history for a user")
    public ResponseEntity<Page<BorrowResponse>> getUserBorrowHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(borrowService.getUserBorrowHistory(userId, page, size));
    }

    /**
     * GET /api/borrow/overdue
     * Get all overdue books. Useful for admin monitoring.
     */
    @GetMapping("/overdue")
    @Operation(summary = "Get all overdue borrowings (Admin)")
    public ResponseEntity<List<BorrowResponse>> getOverdueBooks() {
        return ResponseEntity.ok(borrowService.getOverdueBooks());
    }
}
