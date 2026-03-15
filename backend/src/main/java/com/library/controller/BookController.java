package com.library.controller;

import com.library.dto.request.BookRequest;
import com.library.dto.response.BookResponse;
import com.library.service.BookService;
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
 * BookController - REST endpoints for book management.
 *
 * GET /api/books → Public (anyone can browse)
 * POST/PUT/DELETE → ADMIN only
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book catalog management")
public class BookController {

    private final BookService bookService;

    /**
     * GET /api/books?page=0&size=10&sortBy=title
     * Returns paginated list of all books. Public endpoint.
     */
    @GetMapping
    @Operation(summary = "Get all books (paginated)")
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy) {
        return ResponseEntity.ok(bookService.getAllBooks(page, size, sortBy));
    }

    /**
     * GET /api/books/search?keyword=harry&page=0&size=10
     * Search books by keyword. Public endpoint.
     */
    @GetMapping("/search")
    @Operation(summary = "Search books by keyword")
    public ResponseEntity<Page<BookResponse>> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.searchBooks(keyword, page, size));
    }

    /**
     * GET /api/books/{id}
     * Get a specific book by ID. Public endpoint.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * POST /api/books
     * Add a new book. ADMIN only.
     * 
     * @PreAuthorize checks role BEFORE executing the method.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new book (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
    }

    /**
     * PUT /api/books/{id}
     * Update existing book. ADMIN only.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a book (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    /**
     * DELETE /api/books/{id}
     * Delete a book. ADMIN only. Cannot delete if active borrows exist.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a book (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
