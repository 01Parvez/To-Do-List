package com.library.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Book entity - represents a book in the library catalog.
 *
 * availableCopies tracks how many copies are currently on the shelf.
 * It decrements when borrowed and increments when returned.
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    /**
     * ISBN (International Standard Book Number) - unique identifier for book edition.
     * Stored as String to handle formats like "978-0-13-468599-1"
     */
    @Column(unique = true, length = 20)
    private String isbn;

    @Column(length = 100)
    private String category;

    @Column(length = 100)
    private String publisher;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(length = 500)
    private String description;

    /** Total number of copies owned by the library */
    @Column(name = "total_copies", nullable = false)
    @Builder.Default
    private Integer totalCopies = 1;

    /**
     * Copies currently available for borrowing.
     * availableCopies <= totalCopies always.
     * Managed by BorrowService on borrow/return operations.
     */
    @Column(name = "available_copies", nullable = false)
    @Builder.Default
    private Integer availableCopies = 1;

    /** Optional path to book cover image */
    @Column(name = "cover_image_path")
    private String coverImagePath;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
