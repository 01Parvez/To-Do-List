package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * BookRepository - data access for Book entity.
 * Uses pagination and custom JPQL queries for search.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /** Check if a book with the given ISBN already exists */
    boolean existsByIsbn(String isbn);

    /** Find book by ISBN */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Full-text search across title, author, category, and isbn.
     *
     * JPQL Query:
     *  - :keyword is bound to the method parameter
     *  - LOWER() makes search case-insensitive
     *  - %:keyword% allows partial matching
     *  - Returns a Page for pagination support
     */
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);

    /** Search books by category only (for category filtering) */
    Page<Book> findByCategoryIgnoreCase(String category, Pageable pageable);

    /** Find all books with pagination */
    Page<Book> findAll(Pageable pageable);
}
