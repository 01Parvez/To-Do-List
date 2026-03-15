-- ================================================================
-- Library Management System - Sample Data
-- ================================================================
-- Admin password: admin123 (BCrypt hash below)
-- User password:  user123  (BCrypt hash below)
-- ================================================================

USE library_db;

-- Roles
INSERT IGNORE INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

-- Users (passwords are BCrypt hashes - generated at runtime by DataInitializer)
-- The app seeds these automatically. This is just for reference.

-- Sample Books
INSERT INTO books (title, author, isbn, category, publisher, publication_year, description, total_copies, available_copies)
VALUES
('The Great Gatsby',         'F. Scott Fitzgerald',  '978-0-7432-7356-5', 'Classic',        'Scribner',       1925, 'A story of the Jazz Age.',                                    5, 5),
('To Kill a Mockingbird',    'Harper Lee',           '978-0-06-112008-4', 'Classic',        'HarperCollins',  1960, 'A novel about racial injustice in the American South.',        3, 3),
('1984',                     'George Orwell',        '978-0-451-52493-5', 'Dystopian',      'Signet Classic', 1949, 'A dystopian social science fiction novel.',                   4, 4),
('Harry Potter and the Sorcerers Stone', 'J.K. Rowling', '978-0-7475-3269-9', 'Fantasy',  'Bloomsbury',     1997, 'The first Harry Potter novel.',                               6, 6),
('Clean Code',               'Robert C. Martin',     '978-0-13-235088-4', 'Technology',    'Prentice Hall',  2008, 'A handbook of agile software craftsmanship.',                 2, 2),
('The Alchemist',            'Paulo Coelho',         '978-0-06-231609-7', 'Fiction',        'HarperOne',      1988, 'A philosophical novel about following your dream.',           5, 5),
('Sapiens',                  'Yuval Noah Harari',    '978-0-06-231609-8', 'History',        'Harper',         2011, 'A brief history of humankind.',                               3, 3),
('The Pragmatic Programmer', 'David Thomas',         '978-0-13-595705-9', 'Technology',    'Addison-Wesley', 1999, 'From journeyman to master programmer.',                       2, 2),
('Atomic Habits',            'James Clear',          '978-0-73-521129-2', 'Self-Help',      'Avery',          2018, 'An easy proven way to build good habits.',                   4, 4),
('The Da Vinci Code',        'Dan Brown',            '978-0-38-550420-5', 'Thriller',       'Doubleday',      2003, 'A mystery thriller novel.',                                  3, 3);
