-- ================================================================
-- Library Management System - Database Schema
-- ================================================================
-- Run this on your MySQL server if Spring Boot auto-create fails.
-- Normally Spring Boot (ddl-auto=update) creates tables automatically.
-- ================================================================

CREATE DATABASE IF NOT EXISTS library_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE library_db;

-- ----------------------------------------------------------------
-- ROLES table
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE  -- ROLE_ADMIN or ROLE_USER
);

-- ----------------------------------------------------------------
-- USERS table
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(120) NOT NULL,          -- BCrypt hash
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------
-- USER_ROLES join table (ManyToMany: User ↔ Role)
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id)  ON DELETE CASCADE
);

-- ----------------------------------------------------------------
-- BOOKS table
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS books (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(200) NOT NULL,
    author           VARCHAR(100) NOT NULL,
    isbn             VARCHAR(20)  UNIQUE,
    category         VARCHAR(100),
    publisher        VARCHAR(100),
    publication_year INT,
    description      VARCHAR(500),
    total_copies     INT          NOT NULL DEFAULT 1,
    available_copies INT          NOT NULL DEFAULT 1,
    cover_image_path VARCHAR(255),
    created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    -- Ensure available copies never exceeds total
    CONSTRAINT chk_copies CHECK (available_copies >= 0 AND available_copies <= total_copies)
);

-- ----------------------------------------------------------------
-- MEMBERS table
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS members (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    phone           VARCHAR(15),
    membership_date DATE,
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE or INACTIVE
    user_id         BIGINT       UNIQUE,                     -- linked user account
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ----------------------------------------------------------------
-- BORROWS table
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS borrows (
    id          BIGINT         AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT         NOT NULL,
    book_id     BIGINT         NOT NULL,
    borrow_date DATE           NOT NULL,
    due_date    DATE           NOT NULL,
    return_date DATE,                          -- NULL until returned
    status      VARCHAR(20)    NOT NULL DEFAULT 'BORROWED',  -- BORROWED/RETURNED/OVERDUE
    fine_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at  DATETIME       DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id)  ON DELETE CASCADE
);

-- ----------------------------------------------------------------
-- Indexes for performance
-- ----------------------------------------------------------------
CREATE INDEX idx_books_title     ON books(title);
CREATE INDEX idx_books_author    ON books(author);
CREATE INDEX idx_books_category  ON books(category);
CREATE INDEX idx_borrows_user    ON borrows(user_id);
CREATE INDEX idx_borrows_book    ON borrows(book_id);
CREATE INDEX idx_borrows_status  ON borrows(status);
CREATE INDEX idx_borrows_due     ON borrows(due_date);
