package com.municipality.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"authors", "loans", "ratings"})
@ToString(exclude = {"authors", "loans", "ratings"})
public class Book {
    @Id
    @Column(name = "isbn", nullable = false, length = 20)
    private String isbn;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "genre", length = 50)
    private String genre;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies = 1;

    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies = 1;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Many-to-Many με Authors - Book είναι η owning side
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "book_authors",
        joinColumns = @JoinColumn(name = "book_isbn"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    // One-to-Many με Loans
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<Loan> loans = new HashSet<>();

    // One-to-Many με Ratings
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<BookRating> ratings = new HashSet<>();

    // Helper methods από το M2M example
    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
        author.getBooks().remove(this);
    }

    // Business logic methods
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void borrowBook() {
        if (availableCopies > 0) {
            availableCopies--;
        }
    }

    public void returnBook() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    // Calculate average rating
    public Double getAverageRating() {
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
            .mapToInt(BookRating::getRating)
            .average()
            .orElse(0.0);
    }
    
    // Get authors as comma-separated string
    public String getAuthorsString() {
        if (authors.isEmpty()) {
            return "Unknown Author";
        }
        return authors.stream()
            .map(Author::getName)
            .reduce((a, b) -> a + ", " + b)
            .orElse("Unknown Author");
    }
}
