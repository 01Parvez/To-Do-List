package com.library.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * BorrowRequest DTO - carries the bookId when a user wants to borrow a book.
 * The userId is extracted from the JWT token (not sent in body for security).
 */
@Data
public class BorrowRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;
}
