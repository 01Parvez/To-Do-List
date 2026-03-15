package com.library.service;

import com.library.dto.request.BorrowRequest;
import com.library.dto.response.BorrowResponse;
import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.entity.User;
import com.library.exception.BusinessException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BorrowService - core business logic for the borrowing system.
 *
 * Key operations:
 * 1. borrowBook() - validates availability, decrements copies, creates record
 * 2. returnBook() - increments copies back, calculates fine if overdue
 * 3. getHistory() - paginated borrow history per user
 * 4. calculateFine() - fine = days_late * fine_per_day
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /** Fine per day from application.properties */
    @Value("${app.fine.per-day:10}")
    private BigDecimal finePerDay;

    /** Default borrow duration from application.properties */
    @Value("${app.borrow.duration-days:14}")
    private int borrowDurationDays;

    /**
     * Borrow a book for the authenticated user.
     *
     * Business rules:
     * 1. Book must exist
     * 2. At least 1 copy must be available
     * 3. User must not already have this book borrowed (active borrow)
     * 4. Decrement availableCopies by 1
     *
     * @param username - extracted from JWT token (authenticated user)
     * @param request  - contains bookId
     */
    public BorrowResponse borrowBook(String username, BorrowRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", request.getBookId()));

        // Rule 1: Check availability
        if (book.getAvailableCopies() <= 0) {
            throw new BusinessException("No copies available for: \"" + book.getTitle() + "\"");
        }

        // Rule 2: Check if user already has this book borrowed (prevent duplicate
        // active borrow)
        borrowRepository.findByUserIdAndBookIdAndStatus(
                user.getId(), book.getId(), Borrow.BorrowStatus.BORROWED)
                .ifPresent(b -> {
                    throw new BusinessException("You already have \"" + book.getTitle() + "\" borrowed");
                });

        // Rule 3: Decrement available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // Rule 4: Create borrow record
        LocalDate today = LocalDate.now();
        Borrow borrow = Borrow.builder()
                .user(user)
                .book(book)
                .borrowDate(today)
                .dueDate(today.plusDays(borrowDurationDays)) // due in 14 days
                .status(Borrow.BorrowStatus.BORROWED)
                .fineAmount(BigDecimal.ZERO)
                .build();

        Borrow saved = borrowRepository.save(borrow);
        log.info("User '{}' borrowed book '{}' (due: {})", username, book.getTitle(), saved.getDueDate());

        return mapToResponse(saved);
    }

    /**
     * Return a borrowed book.
     *
     * Steps:
     * 1. Find the active borrow record
     * 2. Calculate fine if overdue
     * 3. Set return date and status = RETURNED
     * 4. Increment availableCopies back
     *
     * @param borrowId - the borrow record ID to return
     */
    public BorrowResponse returnBook(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record", "id", borrowId));

        // Validate that the book hasn't already been returned
        if (borrow.getStatus() == Borrow.BorrowStatus.RETURNED) {
            throw new BusinessException("This book has already been returned");
        }

        LocalDate today = LocalDate.now();
        borrow.setReturnDate(today);
        borrow.setStatus(Borrow.BorrowStatus.RETURNED);

        // FINE CALCULATION:
        // If returned after dueDate, calculate overdue fine
        // Formula: fine = daysLate * finePerDay
        if (today.isAfter(borrow.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(borrow.getDueDate(), today);
            BigDecimal fine = finePerDay.multiply(BigDecimal.valueOf(daysLate));
            borrow.setFineAmount(fine);
            log.info("Fine calculated for borrow id={}: {} days late = ₹{}", borrowId, daysLate, fine);
        }

        // Return the book copy back to the shelf
        Book book = borrow.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        Borrow updated = borrowRepository.save(borrow);
        log.info("Book '{}' returned by user '{}'", book.getTitle(), borrow.getUser().getUsername());

        return mapToResponse(updated);
    }

    /**
     * Get paginated borrow history for a specific user.
     * Returns all records (BORROWED, RETURNED, OVERDUE).
     */
    @Transactional(readOnly = true)
    public Page<BorrowResponse> getUserBorrowHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());
        return borrowRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get currently active (not returned) borrows for a user.
     */
    @Transactional(readOnly = true)
    public List<BorrowResponse> getActiveBorrows(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return borrowRepository.findByUserIdAndStatus(user.getId(), Borrow.BorrowStatus.BORROWED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all overdue books (admin function).
     * Marks them as OVERDUE in the database.
     */
    @Transactional
    public List<BorrowResponse> getOverdueBooks() {
        LocalDate today = LocalDate.now();
        List<Borrow> overdue = borrowRepository.findOverdueBorrows(today);

        // Update status to OVERDUE for all found records
        overdue.forEach(b -> {
            if (b.getStatus() == Borrow.BorrowStatus.BORROWED) {
                b.setStatus(Borrow.BorrowStatus.OVERDUE);
                borrowRepository.save(b);
            }
        });

        return overdue.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Map Borrow entity → BorrowResponse DTO.
     * Includes calculated overdue status and days overdue.
     */
    public BorrowResponse mapToResponse(Borrow borrow) {
        LocalDate today = LocalDate.now();
        long daysOverdue = ChronoUnit.DAYS.between(borrow.getDueDate(), today);

        return BorrowResponse.builder()
                .id(borrow.getId())
                .userId(borrow.getUser().getId())
                .username(borrow.getUser().getUsername())
                .bookId(borrow.getBook().getId())
                .bookTitle(borrow.getBook().getTitle())
                .bookAuthor(borrow.getBook().getAuthor())
                .bookIsbn(borrow.getBook().getIsbn())
                .borrowDate(borrow.getBorrowDate())
                .dueDate(borrow.getDueDate())
                .returnDate(borrow.getReturnDate())
                .status(borrow.getStatus())
                .fineAmount(borrow.getFineAmount())
                .overdue(daysOverdue > 0 && borrow.getReturnDate() == null)
                .daysOverdue(Math.max(0, daysOverdue))
                .createdAt(borrow.getCreatedAt())
                .build();
    }
}
