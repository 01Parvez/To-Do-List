package com.library.dto.response;

import com.library.entity.Borrow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * BorrowResponse DTO - returned for borrow/return operations and history.
 * Flattens the Book and User relationships for easy frontend consumption.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowResponse {

    private Long id;

    /** User information (flattened) */
    private Long userId;
    private String username;

    /** Book information (flattened) */
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;

    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Borrow.BorrowStatus status;
    private BigDecimal fineAmount;

    /** True if the book is past its due date */
    private boolean overdue;

    /** Number of days overdue (negative if not overdue) */
    private long daysOverdue;

    private LocalDateTime createdAt;
}
