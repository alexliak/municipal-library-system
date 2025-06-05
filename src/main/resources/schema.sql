-- Create database
CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

-- Drop tables if exist (for clean setup)
DROP TABLE IF EXISTS book_ratings;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS book_authors;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

-- Create roles table
CREATE TABLE roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Create users table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enabled BOOLEAN DEFAULT TRUE
);

-- Create user_roles join table
CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

-- Create authors table
CREATE TABLE authors (
    author_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    biography TEXT,
    birth_date DATE,
    nationality VARCHAR(50)
);

-- Create books table
CREATE TABLE books (
    isbn VARCHAR(13) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    publication_date DATE,
    genre VARCHAR(50),
    summary TEXT,
    cover_image_url VARCHAR(500),
    total_copies INT DEFAULT 1,
    available_copies INT DEFAULT 1,
    CONSTRAINT chk_copies CHECK (available_copies <= total_copies AND available_copies >= 0)
);

-- Create book_authors join table
CREATE TABLE book_authors (
    book_isbn VARCHAR(13) NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (book_isbn, author_id),
    FOREIGN KEY (book_isbn) REFERENCES books(isbn) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE CASCADE
);

-- Create loans table
CREATE TABLE loans (
    loan_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_isbn VARCHAR(13) NOT NULL,
    user_id INT NOT NULL,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (book_isbn) REFERENCES books(isbn),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_due_date (due_date)
);

-- Create book_ratings table (optional feature)
CREATE TABLE book_ratings (
    rating_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_isbn VARCHAR(13) NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_isbn) REFERENCES books(isbn),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE KEY unique_user_book_rating (user_id, book_isbn)
);

-- Insert initial roles
INSERT INTO roles (name, description) VALUES 
('ROLE_ADMIN', 'Full system administrator with all permissions'),
('ROLE_LIBRARIAN', 'Library staff with book and loan management permissions'),
('ROLE_MEMBER', 'Library member with borrowing privileges');

-- Create indexes for better performance
CREATE INDEX idx_book_title ON books(title);
CREATE INDEX idx_book_genre ON books(genre);
CREATE INDEX idx_author_name ON authors(name);
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);
