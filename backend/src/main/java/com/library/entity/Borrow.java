package com.library.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Borrow entity - records a borrowing transaction.
 *
 * Lifecycle:
 *  1. User borrows book → status = BORROWED, returnDate = null
 *  2. User returns book → status = RETURNED, returnDate = today
 *  3. If overdue → fineAmount calculated (days_late * fine_per_day)
 *
 * This single entity tracks both active borrows and history.
 */
@Entity
@Table(name = "borrows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who borrowed the book.
     * ManyToOne: one user can have many borrow records.
     * LAZY fetch: user loaded only when accessed (performance optimization).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The book that was borrowed.
     * ManyToOne: one book can have many borrow records over time.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /** Date the book was borrowed */
    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    /**
     * Date by which the book must be returned.
     * Set to borrowDate + configurable duration (default 14 days).
     */
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /**
     * Actual return date - null until the book is returned.
     * Used to calculate if return is overdue.
     */
    @Column(name = "return_date")
    private LocalDate returnDate;

    /**
     * Current status of this borrow record.
     * BORROWED: book is currently with the user.
     * RETURNED: book has been returned.
     * OVERDUE: passed due date and not yet returned.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private BorrowStatus status = BorrowStatus.BORROWED;

    /**
     * Fine amount in rupees.
     * Formula: max(0, daysLate) * finePerDay
     * 0 if returned on time. Stored to avoid recalculation.
     */
    @Column(name = "fine_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal fineAmount = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Enum for borrow transaction status */
    public enum BorrowStatus {
        BORROWED,
        RETURNED,
        OVERDUE
    }
}
