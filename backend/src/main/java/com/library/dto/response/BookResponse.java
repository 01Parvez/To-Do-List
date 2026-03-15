package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * BookResponse DTO - sent to client when returning book data.
 * Separates the API response from the internal entity structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String publisher;
    private Integer publicationYear;
    private String description;
    private Integer totalCopies;
    private Integer availableCopies;

    /** True if at least 1 copy is available to borrow */
    private boolean available;

    private String coverImagePath;
    private LocalDateTime createdAt;
}
