package com.municipality.library.controller;

import com.municipality.library.entity.Book;
import com.municipality.library.service.AuthorService;
import com.municipality.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private AuthorService authorService;
    
    @GetMapping
    public String listBooks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "title") String sort,
            @RequestParam(value = "search", required = false) String search,
            ModelMap model) {
        
        System.out.println("Listing books - page: " + page + ", size: " + size + ", sort: " + sort + ", search: " + search);
        
        Page<Book> bookPage;
        
        if (search != null && !search.trim().isEmpty()) {
            // For search, we'll use a simple list (pagination can be added later)
            List<Book> searchResults = bookService.searchBooks(search);
            model.addAttribute("books", searchResults);
            model.addAttribute("search", search);
            model.addAttribute("totalBooks", searchResults.size());
        } else {
            // Regular paginated listing
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sort));
            bookPage = bookService.findBooksPageable(pageRequest);
            
            model.addAttribute("books", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());
            model.addAttribute("totalBooks", bookPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("sort", sort);
        }
        
        return "books/list";
    }
    
    @GetMapping("/{isbn}")
    public String viewBook(@PathVariable String isbn, ModelMap model) {
        
        Optional<Book> book = bookService.findByIsbn(isbn);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "books/detail";
        } else {
            return "redirect:/books?error=notfound";
        }
    }
    
    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String showAddBookForm(ModelMap model) {
        
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", getGenreList());
        
        return "books/form";
    }
    
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String addBook(
            @Valid @ModelAttribute Book book,
            BindingResult result,
            @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
            RedirectAttributes redirectAttributes,
            ModelMap model) {
        
        if (result.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", getGenreList());
            return "books/form";
        }
        
        // Check if ISBN already exists
        if (bookService.findByIsbn(book.getIsbn()).isPresent()) {
            result.rejectValue("isbn", "error.book", "ISBN already exists!");
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", getGenreList());
            return "books/form";
        }
        
        // Process author associations
        if (authorIds != null && !authorIds.isEmpty()) {
            authorIds.forEach(authorId -> {
                authorService.findById(authorId).ifPresent(author -> {
                    book.addAuthor(author);
                });
            });
        }
        
        try {
            bookService.saveBook(book);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Book '" + book.getTitle() + "' added successfully!");
            return "redirect:/books";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding book: " + e.getMessage());
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", getGenreList());
            return "books/form";
        }
    }
    
    @GetMapping("/edit/{isbn}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String showEditBookForm(@PathVariable String isbn, ModelMap model) {
        
        Optional<Book> book = bookService.findByIsbn(isbn);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", getGenreList());
            model.addAttribute("selectedAuthorIds", 
                book.get().getAuthors().stream()
                    .map(author -> author.getAuthorId())
                    .toList()
            );
            return "books/form";
        } else {
            return "redirect:/books?error=notfound";
        }
    }
    
    @PostMapping("/edit/{isbn}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String updateBook(
            @PathVariable String isbn,
            @Valid @ModelAttribute Book book,
            BindingResult result,
            @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
            RedirectAttributes redirectAttributes,
            ModelMap model) {
        
        
        if (result.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", getGenreList());
            return "books/form";
        }
        
        // Clear and re-add authors
        book.getAuthors().clear();
        if (authorIds != null && !authorIds.isEmpty()) {
            authorIds.forEach(authorId -> {
                authorService.findById(authorId).ifPresent(author -> {
                    book.addAuthor(author);
                });
            });
        }
        
        try {
            bookService.updateBook(isbn, book);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Book '" + book.getTitle() + "' updated successfully!");
            return "redirect:/books/" + isbn;
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating book: " + e.getMessage());
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", getGenreList());
            return "books/form";
        }
    }
    
    @PostMapping("/delete/{isbn}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteBook(
            @PathVariable String isbn,
            RedirectAttributes redirectAttributes) {
        
        
        try {
            Optional<Book> book = bookService.findByIsbn(isbn);
            if (book.isPresent()) {
                bookService.deleteBook(isbn);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Book '" + book.get().getTitle() + "' deleted successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting book: " + e.getMessage());
        }
        
        return "redirect:/books";
    }
    
    @GetMapping("/search")
    public String showSearchPage(ModelMap model) {
        model.addAttribute("genres", getGenreList());
        return "books/search";
    }
    
    @GetMapping("/search/results")
    public String searchBooks(
            @RequestParam("query") String query,
            ModelMap model) {
        
        
        List<Book> results = bookService.searchBooks(query);
        model.addAttribute("books", results);
        model.addAttribute("search", query);
        model.addAttribute("totalBooks", results.size());
        
        return "books/list";
    }
    
    @GetMapping("/genre/{genre}")
    public String booksByGenre(
            @PathVariable String genre,
            ModelMap model) {
        
        
        List<Book> books = bookService.findByGenre(genre);
        model.addAttribute("books", books);
        model.addAttribute("genre", genre);
        model.addAttribute("totalBooks", books.size());
        
        return "books/list";
    }
    
    @GetMapping("/available")
    public String availableBooks(ModelMap model) {
        
        List<Book> books = bookService.findAvailableBooks();
        model.addAttribute("books", books);
        model.addAttribute("filter", "Available Books");
        model.addAttribute("totalBooks", books.size());
        
        return "books/list";
    }
    
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String lowStockBooks(
            @RequestParam(value = "threshold", defaultValue = "5") int threshold,
            ModelMap model) {
        
        
        List<Book> books = bookService.findLowStockBooks(threshold);
        model.addAttribute("books", books);
        model.addAttribute("filter", "Low Stock (≤ " + threshold + " copies)");
        model.addAttribute("totalBooks", books.size());
        model.addAttribute("threshold", threshold);
        
        return "books/list";
    }
    
    private List<String> getGenreList() {
        return List.of(
            "Fiction",
            "Non-Fiction",
            "Science Fiction",
            "Mystery",
            "Romance",
            "Thriller",
            "Biography",
            "History",
            "Science",
            "Technology",
            "Business",
            "Self-Help",
            "Children",
            "Young Adult",
            "Poetry",
            "Art",
            "Travel",
            "Cooking",
            "Health",
            "Education"
        );
    }
}
