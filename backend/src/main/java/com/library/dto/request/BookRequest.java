package com.library.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * BookRequest DTO - used for both Create and Update book operations.
 */
@Data
public class BookRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be under 200 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 100)
    private String author;

    @Size(max = 20, message = "ISBN must be under 20 characters")
    private String isbn;

    private String category;
    private String publisher;

    @Min(value = 1000, message = "Publication year must be valid")
    @Max(value = 2100)
    private Integer publicationYear;

    private String description;

    @NotNull(message = "Total copies required")
    @Min(value = 1, message = "Must have at least 1 copy")
    private Integer totalCopies;
}
