package com.municipality.library.config;

import com.municipality.library.entity.*;
import com.municipality.library.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    CommandLineRunner init(UserRepository userRepository,
                           RoleRepository roleRepository,
                           BookRepository bookRepository,
                           AuthorRepository authorRepository,
                           LoanRepository loanRepository) {

        return args -> {
            // Check if USERS exist - not roles!
            if (userRepository.count() > 0) {
                System.out.println("Users already exist, skipping initialization");
                return;
            }

            System.out.println("Initializing database with sample data...");

            // Get existing roles or create new ones
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role r = new Role("ROLE_ADMIN", "Administrator with full access");
                        return roleRepository.save(r);
                    });

            Role librarianRole = roleRepository.findByName("ROLE_LIBRARIAN")
                    .orElseGet(() -> {
                        Role r = new Role("ROLE_LIBRARIAN", "Librarian with book and loan management access");
                        return roleRepository.save(r);
                    });

            Role memberRole = roleRepository.findByName("ROLE_MEMBER")
                    .orElseGet(() -> {
                        Role r = new Role("ROLE_MEMBER", "Library member with borrowing privileges");
                        return roleRepository.save(r);
                    });

            // Create Users
            // Admin User
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@library.com");
            admin.setFullName("System Administrator");
            admin.setEnabled(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
            userRepository.save(admin);

            // Librarian User
            User librarian = new User();
            librarian.setUsername("librarian");
            librarian.setPassword(passwordEncoder.encode("librarian123"));
            librarian.setEmail("librarian@library.com");
            librarian.setFullName("Sarah Johnson");
            librarian.setEnabled(true);
            librarian.setCreatedAt(LocalDateTime.now());
            librarian.setRoles(new HashSet<>(Arrays.asList(librarianRole)));
            userRepository.save(librarian);

            // Member User
            User member = new User();
            member.setUsername("member");
            member.setPassword(passwordEncoder.encode("member123"));
            member.setEmail("member@example.com");
            member.setFullName("Jane Reader");
            member.setPhone("5555555555");
            member.setAddress("123 Book Street");
            member.setEnabled(true);
            member.setCreatedAt(LocalDateTime.now());
            member.setRoles(new HashSet<>(Arrays.asList(memberRole)));
            userRepository.save(member);

            // Additional member for testing
            User john = new User();
            john.setUsername("john");
            john.setPassword(passwordEncoder.encode("john123"));
            john.setEmail("john.doe@email.com");
            john.setFullName("John Doe");
            john.setEnabled(true);
            john.setCreatedAt(LocalDateTime.now());
            john.setRoles(new HashSet<>(Arrays.asList(memberRole)));
            userRepository.save(john);

            // Create Authors
            Author author1 = new Author();
            author1.setName("George Orwell");
            author1.setBiography("English novelist and essayist, journalist and critic.");
            author1.setBirthDate(LocalDate.of(1903, 6, 25));
            author1.setNationality("British");
            author1 = authorRepository.save(author1);

            Author author2 = new Author();
            author2.setName("J.K. Rowling");
            author2.setBiography("British author, best known for the Harry Potter series.");
            author2.setBirthDate(LocalDate.of(1965, 7, 31));
            author2.setNationality("British");
            author2 = authorRepository.save(author2);

            Author author3 = new Author();
            author3.setName("Harper Lee");
            author3.setBiography("American novelist widely known for To Kill a Mockingbird.");
            author3.setBirthDate(LocalDate.of(1926, 4, 28));
            author3.setNationality("American");
            author3 = authorRepository.save(author3);

            Author author4 = new Author();
            author4.setName("F. Scott Fitzgerald");
            author4.setBiography("American novelist and short story writer.");
            author4.setBirthDate(LocalDate.of(1896, 9, 24));
            author4.setNationality("American");
            author4 = authorRepository.save(author4);

            // Create Books
            Book book1 = new Book();
            book1.setIsbn("978-0451524935");
            book1.setTitle("1984");
            book1.setGenre("Fiction");
            book1.setPublicationDate(LocalDate.of(1949, 6, 8));
            book1.setSummary("A dystopian social science fiction novel and cautionary tale.");
            book1.setCoverImageUrl("https://covers.openlibrary.org/b/isbn/978-0451524935-L.jpg");
            book1.setTotalCopies(5);
            book1.setAvailableCopies(3);
            book1.setCreatedAt(LocalDateTime.now());
            book1.addAuthor(author1);
            bookRepository.save(book1);

            Book book2 = new Book();
            book2.setIsbn("978-0439708180");
            book2.setTitle("Harry Potter and the Sorcerer's Stone");
            book2.setGenre("Fiction");
            book2.setPublicationDate(LocalDate.of(1997, 6, 26));
            book2.setSummary("The first novel in the Harry Potter series.");
            book2.setCoverImageUrl("https://covers.openlibrary.org/b/isbn/978-0439708180-L.jpg");
            book2.setTotalCopies(10);
            book2.setAvailableCopies(7);
            book2.setCreatedAt(LocalDateTime.now());
            book2.addAuthor(author2);
            bookRepository.save(book2);

            Book book3 = new Book();
            book3.setIsbn("978-0061120084");
            book3.setTitle("To Kill a Mockingbird");
            book3.setGenre("Fiction");
            book3.setPublicationDate(LocalDate.of(1960, 7, 11));
            book3.setSummary("A novel about the serious issues of rape and racial inequality.");
            book3.setCoverImageUrl("https://covers.openlibrary.org/b/isbn/978-0061120084-L.jpg");
            book3.setTotalCopies(4);
            book3.setAvailableCopies(2);
            book3.setCreatedAt(LocalDateTime.now());
            book3.addAuthor(author3);
            bookRepository.save(book3);

            Book book4 = new Book();
            book4.setIsbn("978-0743273565");
            book4.setTitle("The Great Gatsby");
            book4.setGenre("Fiction");
            book4.setPublicationDate(LocalDate.of(1925, 4, 10));
            book4.setSummary("A story of the mysteriously wealthy Jay Gatsby.");
            book4.setCoverImageUrl("https://covers.openlibrary.org/b/isbn/978-0743273565-L.jpg");
            book4.setTotalCopies(6);
            book4.setAvailableCopies(4);
            book4.setCreatedAt(LocalDateTime.now());
            book4.addAuthor(author4);
            bookRepository.save(book4);

            Book book5 = new Book();
            book5.setIsbn("978-0452284234");
            book5.setTitle("Animal Farm");
            book5.setGenre("Fiction");
            book5.setPublicationDate(LocalDate.of(1945, 8, 17));
            book5.setSummary("An allegorical novella reflecting events leading up to the Russian Revolution.");
            book5.setCoverImageUrl("https://covers.openlibrary.org/b/isbn/978-0452284234-L.jpg");
            book5.setTotalCopies(3);
            book5.setAvailableCopies(1);
            book5.setCreatedAt(LocalDateTime.now());
            book5.addAuthor(author1);
            bookRepository.save(book5);

            // Create some sample loans
            Loan loan1 = new Loan();
            loan1.setUser(member);
            loan1.setBook(book1);
            loan1.setLoanDate(LocalDateTime.now().minusDays(5));
            loan1.setDueDate(LocalDateTime.now().plusDays(9));
            loan1.setStatus(LoanStatus.ACTIVE);
            loanRepository.save(loan1);

            Loan loan2 = new Loan();
            loan2.setUser(john);
            loan2.setBook(book2);
            loan2.setLoanDate(LocalDateTime.now().minusDays(10));
            loan2.setDueDate(LocalDateTime.now().plusDays(4));
            loan2.setStatus(LoanStatus.ACTIVE);
            loanRepository.save(loan2);

            // Create an overdue loan
            Loan loan3 = new Loan();
            loan3.setUser(member);
            loan3.setBook(book5);
            loan3.setLoanDate(LocalDateTime.now().minusDays(20));
            loan3.setDueDate(LocalDateTime.now().minusDays(6));
            loan3.setStatus(LoanStatus.ACTIVE);
            loanRepository.save(loan3);

            System.out.println("Sample data initialized successfully!");
            System.out.println("==================================");
            System.out.println("Demo Users:");
            System.out.println("Admin: admin / admin123");
            System.out.println("Librarian: librarian / librarian123");
            System.out.println("Member: member / member123");
            System.out.println("Additional: john / john123");
            System.out.println("==================================");
        };
    }
}