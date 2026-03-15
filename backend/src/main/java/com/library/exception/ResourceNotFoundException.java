package com.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ResourceNotFoundException - thrown when requested entity is not found in DB.
 * @ResponseStatus maps this to HTTP 404 Not Found automatically.
 *
 * Example: GET /api/books/999 → "Book not found with id: 999"
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Convenience constructor for "Entity not found with field: value" pattern.
     * Example: new ResourceNotFoundException("Book", "id", 5)
     *          → "Book not found with id: 5"
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }
}
