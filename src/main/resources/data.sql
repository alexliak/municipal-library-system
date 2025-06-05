-- Sample data for testing

-- Insert sample authors
INSERT INTO authors (name, biography, birth_date, nationality) VALUES
('George Orwell', 'English novelist and essayist, journalist and critic.', '1903-06-25', 'British'),
('J.K. Rowling', 'British author, best known for the Harry Potter series.', '1965-07-31', 'British'),
('Harper Lee', 'American novelist widely known for To Kill a Mockingbird.', '1926-04-28', 'American'),
('Gabriel García Márquez', 'Colombian novelist, short-story writer, and journalist.', '1927-03-06', 'Colombian'),
('Agatha Christie', 'English writer known for her detective novels.', '1890-09-15', 'British');

-- Insert sample books
INSERT INTO books (isbn, title, publication_date, genre, summary, cover_image_url, total_copies, available_copies) VALUES
('9780451524935', '1984', '1949-06-08', 'Dystopian Fiction', 'A dystopian social science fiction novel and cautionary tale about the dangers of totalitarianism.', 'https://example.com/1984.jpg', 5, 5),
('9780439708180', 'Harry Potter and the Sorcerer\'s Stone', '1997-06-26', 'Fantasy', 'The first novel in the Harry Potter series, it follows Harry Potter, a young wizard.', 'https://example.com/hp1.jpg', 10, 10),
('9780061120084', 'To Kill a Mockingbird', '1960-07-11', 'Southern Gothic', 'The story of racial injustice and childhood innocence in the American South.', 'https://example.com/mockingbird.jpg', 3, 3),
('9780307474728', 'One Hundred Years of Solitude', '1967-05-30', 'Magic Realism', 'The multi-generational story of the Buendía family.', 'https://example.com/solitude.jpg', 4, 4),
('9780062073501', 'Murder on the Orient Express', '1934-01-01', 'Mystery', 'A murder mystery set on the famous Orient Express train.', 'https://example.com/orient.jpg', 6, 6);

-- Insert book-author relationships
INSERT INTO book_authors (book_isbn, author_id) VALUES
('9780451524935', 1),  -- 1984 by George Orwell
('9780439708180', 2),  -- Harry Potter by J.K. Rowling
('9780061120084', 3),  -- To Kill a Mockingbird by Harper Lee
('9780307474728', 4),  -- One Hundred Years of Solitude by Gabriel García Márquez
('9780062073501', 5);  -- Murder on the Orient Express by Agatha Christie

-- Note: Users will be created by the DataInitializer class with encrypted passwords
