package com.municipality.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"books"})
@ToString(exclude = {"books"})
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    // Many-to-Many με Books - Author είναι η non-owning side
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    // Constructor για εύκολη δημιουργία
    public Author(String name) {
        this.name = name;
    }


    // Helper method για να πάρουμε τον αριθμό βιβλίων
    public int getBookCount() {
        return books.size();
    }

    public void addBook(Book book) {
        if (books == null) {
            books = new HashSet<>();
        }
        books.add(book);
        book.getAuthors().add(this);
    }

    public void removeBook(Book book) {
        if (books != null) {
            books.remove(book);
            book.getAuthors().remove(this);
        }
    }


}
