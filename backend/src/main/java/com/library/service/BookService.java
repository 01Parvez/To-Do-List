package com.library.service;

import com.library.dto.request.BookRequest;
import com.library.dto.response.BookResponse;
import com.library.entity.Book;
import com.library.exception.BusinessException;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * BookService - business logic for book management.
 *
 * Responsibilities:
 * - CRUD operations for books
 * - Search with pagination
 * - Copy availability management (coordinated with BorrowService)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;

    /**
     * Get all books with pagination and sorting.
     *
     * @param page   - page number (0-indexed)
     * @param size   - items per page
     * @param sortBy - field to sort by (e.g., "title", "author")
     */
    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooks(int page, int size, String sortBy) {
        // Pageable: tells JPA which page to return and how to sort
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return bookRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Search books by keyword across title, author, category, isbn.
     */
    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooks(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        return bookRepository.searchBooks(keyword, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get a single book by ID.
     * Throws ResourceNotFoundException (404) if not found.
     */
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        return mapToResponse(book);
    }

    /**
     * Create a new book.
     * Validates ISBN uniqueness.
     *
     * New books start with availableCopies = totalCopies.
     */
    public BookResponse createBook(BookRequest request) {
        // Check ISBN uniqueness (only if ISBN provided)
        if (request.getIsbn() != null && !request.getIsbn().isBlank()
                && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException("Book already exists with ISBN: " + request.getIsbn());
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .category(request.getCategory())
                .publisher(request.getPublisher())
                .publicationYear(request.getPublicationYear())
                .description(request.getDescription())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies()) // all copies available initially
                .build();

        Book saved = bookRepository.save(book);
        log.info("Created book: {} (id={})", saved.getTitle(), saved.getId());
        return mapToResponse(saved);
    }

    /**
     * Update an existing book.
     * Adjusts availableCopies proportionally if totalCopies changed.
     */
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        // If admin is changing total copies, adjust available copies accordingly
        if (!book.getTotalCopies().equals(request.getTotalCopies())) {
            int currentlyBorrowed = book.getTotalCopies() - book.getAvailableCopies();
            int newAvailable = request.getTotalCopies() - currentlyBorrowed;
            if (newAvailable < 0) {
                throw new BusinessException(
                        "Cannot reduce total copies below currently borrowed count: " + currentlyBorrowed);
            }
            book.setAvailableCopies(newAvailable);
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setCategory(request.getCategory());
        book.setPublisher(request.getPublisher());
        book.setPublicationYear(request.getPublicationYear());
        book.setDescription(request.getDescription());
        book.setTotalCopies(request.getTotalCopies());

        Book updated = bookRepository.save(book);
        log.info("Updated book id={}", id);
        return mapToResponse(updated);
    }

    /**
     * Delete a book by ID.
     * Cannot delete if the book currently has active borrows.
     */
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        // Check for active borrows
        long activeBorrows = borrowRepository.findByBookId(id).stream()
                .filter(b -> b.getStatus() == com.library.entity.Borrow.BorrowStatus.BORROWED)
                .count();
        if (activeBorrows > 0) {
            throw new BusinessException("Cannot delete book with " + activeBorrows + " active borrows");
        }

        bookRepository.delete(book);
        log.info("Deleted book id={}", id);
    }

    /**
     * Map Book entity → BookResponse DTO.
     * Keeps entity details out of the API response.
     */
    public BookResponse mapToResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .category(book.getCategory())
                .publisher(book.getPublisher())
                .publicationYear(book.getPublicationYear())
                .description(book.getDescription())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .available(book.getAvailableCopies() > 0)
                .coverImagePath(book.getCoverImagePath())
                .createdAt(book.getCreatedAt())
                .build();
    }
}
