package com.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BusinessException - thrown when a business rule is violated.
 * Examples:
 *  - No available copies to borrow
 *  - User already has this book borrowed
 *  - Inactive member trying to borrow
 *
 * Maps to HTTP 400 Bad Request.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
