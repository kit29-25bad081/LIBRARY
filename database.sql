-- ====================================================================
-- DIGITAL LIBRARY MANAGEMENT SYSTEM
-- Non-destructive database setup script for MySQL
-- Compatible with: MySQL 8.x / MySQL Workbench / phpMyAdmin / CLI
-- ====================================================================

-- 1. Create Database
CREATE DATABASE IF NOT EXISTS digital_library 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE digital_library;

-- 2. Create missing tables without deleting existing tables or data.

-- ====================================================================
-- TABLE 1: admins
-- Stores library administrator credentials
-- ====================================================================
CREATE TABLE IF NOT EXISTS admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- ====================================================================
-- TABLE 2: users
-- Stores library members (students, faculty, etc.)
-- ====================================================================
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ====================================================================
-- TABLE 3: ebooks
-- Stores the catalog of digital library e-books
-- ====================================================================
CREATE TABLE IF NOT EXISTS ebooks (
    ebook_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    publisher VARCHAR(100) NOT NULL,
    publication_year INT NOT NULL,
    ebook_url VARCHAR(500) NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ebooks_title (title),
    INDEX idx_ebooks_author (author),
    INDEX idx_ebooks_category (category),
    INDEX idx_ebooks_available (available)
) ENGINE=InnoDB;

-- ====================================================================
-- TABLE 4: issue_records
-- Tracks book borrowings, due dates, returns, and status
-- ====================================================================
CREATE TABLE IF NOT EXISTS issue_records (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    ebook_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ISSUED',
    CONSTRAINT fk_issue_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT fk_issue_ebook FOREIGN KEY (ebook_id) 
        REFERENCES ebooks(ebook_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    INDEX idx_issue_status (status)
) ENGINE=InnoDB;

-- ====================================================================
-- SAMPLE DATA INSERTION
-- ====================================================================

-- 1. Default Admin (Username: admin, Password: admin123)
INSERT INTO admins (username, password) VALUES
('admin', 'admin123')
ON DUPLICATE KEY UPDATE password = 'admin123';

-- 2. Sample Users (5 users)
INSERT IGNORE INTO users (user_id, name, email, phone) VALUES
(1, 'Aarav Sharma', 'aarav.sharma@college.edu', '+91 98765 43210'),
(2, 'Priya Patel', 'priya.patel@college.edu', '+91 98123 45678'),
(3, 'Rohan Verma', 'rohan.verma@college.edu', '+91 97234 56789'),
(4, 'Sneha Mukherjee', 'sneha.m@college.edu', '+91 96345 67890'),
(5, 'Vikram Malhotra', 'vikram.m@college.edu', '+91 95456 78901');

-- 3. Sample E-books (10 e-books)
INSERT IGNORE INTO ebooks (ebook_id, title, author, category, isbn, publisher, publication_year, ebook_url, available) VALUES
(1, 'Effective Java (3rd Edition)', 'Joshua Bloch', 'Computer Science', '978-0134685991', 'Addison-Wesley Professional', 2018, 'https://digitallibrary.org/books/effective-java.pdf', TRUE),
(2, 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', 'Software Engineering', '978-0132350884', 'Prentice Hall', 2008, 'https://digitallibrary.org/books/clean-code.pdf', TRUE),
(3, 'Introduction to Algorithms (4th Edition)', 'Thomas H. Cormen', 'Algorithms', '978-0262046305', 'MIT Press', 2022, 'https://digitallibrary.org/books/clrs-algorithms.pdf', TRUE),
(4, 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Erich Gamma et al.', 'Software Architecture', '978-0201633610', 'Addison-Wesley', 1994, 'https://digitallibrary.org/books/gang-of-four.pdf', TRUE),
(5, 'Database System Concepts (7th Edition)', 'Abraham Silberschatz', 'Databases', '978-0078022159', 'McGraw-Hill Education', 2019, 'https://digitallibrary.org/books/database-concepts.pdf', TRUE),
(6, 'Operating System Concepts (10th Edition)', 'Abraham Silberschatz', 'Systems', '978-1119456339', 'Wiley', 2018, 'https://digitallibrary.org/books/os-concepts.pdf', TRUE),
(7, 'Computer Networking: A Top-Down Approach (8th Edition)', 'James Kurose', 'Networking', '978-0136681557', 'Pearson', 2020, 'https://digitallibrary.org/books/computer-networking.pdf', TRUE),
(8, 'Artificial Intelligence: A Modern Approach (4th Edition)', 'Stuart Russell', 'Artificial Intelligence', '978-0134610993', 'Pearson', 2020, 'https://digitallibrary.org/books/aima.pdf', TRUE),
(9, 'Core Java Volume I--Fundamentals (12th Edition)', 'Cay S. Horstmann', 'Programming Languages', '978-0137871070', 'Oracle Press / Pearson', 2022, 'https://digitallibrary.org/books/core-java-vol1.pdf', TRUE),
(10, 'Head First Java: A Brain-Friendly Guide (3rd Edition)', 'Kathy Sierra & Bert Bates', 'Computer Science', '978-1491910771', 'O Reilly Media', 2022, 'https://digitallibrary.org/books/head-first-java.pdf', TRUE);

-- ====================================================================
-- Verification queries (can be executed to confirm setup)
-- ====================================================================
SELECT 'Admins Count:' AS Metric, COUNT(*) AS Total FROM admins
UNION ALL
SELECT 'Users Count:', COUNT(*) FROM users
UNION ALL
SELECT 'E-books Count:', COUNT(*) FROM ebooks;
