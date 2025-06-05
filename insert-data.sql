-- Clear existing data
DELETE FROM book_authors;
DELETE FROM loans;
DELETE FROM books;
DELETE FROM authors;

-- Insert authors
INSERT INTO authors (author_id, name, biography, birth_date, nationality) VALUES
(1, 'George Orwell', 'English novelist and essayist, journalist and critic.', '1903-06-25', 'British'),
(2, 'J.K. Rowling', 'British author, best known for the Harry Potter series.', '1965-07-31', 'British'),
(3, 'Harper Lee', 'American novelist widely known for To Kill a Mockingbird.', '1926-04-28', 'American'),
(4, 'Gabriel García Márquez', 'Colombian novelist, short-story writer, and journalist.', '1927-03-06', 'Colombian'),
(5, 'Agatha Christie', 'English writer known for her detective novels.', '1890-09-15', 'British');

-- Insert books
INSERT INTO books (isbn, title, publication_date, genre, summary, cover_image_url, total_copies, available_copies) VALUES
('9780451524935', '1984', '1949-06-08', 'Dystopian Fiction', 'A dystopian social science fiction novel and cautionary tale about the dangers of totalitarianism.', 'https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg', 5, 5),
('9780439708180', 'Harry Potter and the Sorcerer\'s Stone', '1997-06-26', 'Fantasy', 'The first novel in the Harry Potter series, it follows Harry Potter, a young wizard.', 'https://covers.openlibrary.org/b/isbn/9780439708180-L.jpg', 10, 10),
('9780061120084', 'To Kill a Mockingbird', '1960-07-11', 'Southern Gothic', 'The story of racial injustice and childhood innocence in the American South.', 'https://covers.openlibrary.org/b/isbn/9780061120084-L.jpg', 3, 3),
('9780307474728', 'One Hundred Years of Solitude', '1967-05-30', 'Magic Realism', 'The multi-generational story of the Buendía family.', 'https://covers.openlibrary.org/b/isbn/9780307474728-L.jpg', 4, 4),
('9780062073501', 'Murder on the Orient Express', '1934-01-01', 'Mystery', 'A murder mystery set on the famous Orient Express train.', 'https://covers.openlibrary.org/b/isbn/9780062073501-L.jpg', 6, 6);

-- Insert book-author relationships
INSERT INTO book_authors (book_isbn, author_id) VALUES
('9780451524935', 1),
('9780439708180', 2),
('9780061120084', 3),
('9780307474728', 4),
('9780062073501', 5);
