package com.library.repository;

import com.library.entity.Borrow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * BorrowRepository - data access for Borrow entity.
 */
@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    /** Get all borrow records for a specific user (with pagination) */
    Page<Borrow> findByUserId(Long userId, Pageable pageable);

    /** Get all ACTIVE borrows (not yet returned) for a user */
    List<Borrow> findByUserIdAndStatus(Long userId, Borrow.BorrowStatus status);

    /**
     * Check if user has an active borrow for this specific book.
     * Prevents borrowing same book twice.
     */
    Optional<Borrow> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, Borrow.BorrowStatus status);

    /**
     * Find all overdue records - where due date has passed and book not returned yet.
     * Used by admin dashboard and scheduled tasks.
     */
    @Query("SELECT b FROM Borrow b WHERE b.status = 'BORROWED' AND b.dueDate < :today")
    List<Borrow> findOverdueBorrows(@Param("today") LocalDate today);

    /** Count total currently borrowed books (for dashboard) */
    long countByStatus(Borrow.BorrowStatus status);

    /** Count overdue books for dashboard */
    @Query("SELECT COUNT(b) FROM Borrow b WHERE b.status = 'BORROWED' AND b.dueDate < :today")
    long countOverdue(@Param("today") LocalDate today);

    /** Get all borrow records for a specific book */
    List<Borrow> findByBookId(Long bookId);
}
