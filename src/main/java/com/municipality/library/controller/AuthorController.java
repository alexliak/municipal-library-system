package com.municipality.library.controller;

import com.municipality.library.entity.Author;
import com.municipality.library.entity.Book;
import com.municipality.library.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/authors")
public class AuthorController {
    
    @Autowired
    private AuthorService authorService;
    
    // List all authors
    @GetMapping
    public String listAuthors(ModelMap model) {
        List<Author> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        model.addAttribute("totalAuthors", authorService.countAuthors());
        return "authors/list";
    }
    
    // Search authors
    @GetMapping("/search")
    public String searchAuthors(@RequestParam("query") String query, ModelMap model) {
        List<Author> authors = authorService.searchByName(query);
        model.addAttribute("authors", authors);
        model.addAttribute("searchQuery", query);
        return "authors/list";
    }
    
    // Show author details with their books
    @GetMapping("/{id}")
    public String showAuthorDetails(@PathVariable Long id, ModelMap model) {
        Optional<Author> authorOpt = authorService.findById(id);
        if (!authorOpt.isPresent()) {
            return "redirect:/authors?error=notfound";
        }
        
        Author author = authorOpt.get();
        List<Book> books = authorService.getBooksByAuthor(id);
        
        model.addAttribute("author", author);
        model.addAttribute("books", books);
        return "authors/detail";
    }
    
    // Show add author form
    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String showAddAuthorForm(ModelMap model) {
        model.addAttribute("author", new Author());
        return "authors/form";
    }
    
    // Process add author form
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String addAuthor(@Valid @ModelAttribute Author author,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "authors/form";
        }
        
        authorService.save(author);
        redirectAttributes.addFlashAttribute("successMessage", "Author added successfully!");
        return "redirect:/authors";
    }
    
    // Show edit author form
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String showEditAuthorForm(@PathVariable Long id, ModelMap model) {
        Optional<Author> authorOpt = authorService.findById(id);
        if (!authorOpt.isPresent()) {
            return "redirect:/authors?error=notfound";
        }
        
        model.addAttribute("author", authorOpt.get());
        return "authors/form";
    }
    
    // Process edit author form
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String updateAuthor(@PathVariable Long id,
                              @Valid @ModelAttribute Author author,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "authors/form";
        }
        
        // Set the ID to ensure we're updating the right entity
        author.setAuthorId(id);
        authorService.save(author);
        
        redirectAttributes.addFlashAttribute("successMessage", "Author updated successfully!");
        return "redirect:/authors";
    }
    
    // Delete author
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAuthor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            authorService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Author deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Cannot delete author with associated books. Remove all book associations first.");
        }
        return "redirect:/authors";
    }
    
    // Authors by nationality
    @GetMapping("/nationality/{nationality}")
    public String authorsByNationality(@PathVariable String nationality, ModelMap model) {
        List<Author> authors = authorService.findByNationality(nationality);
        model.addAttribute("authors", authors);
        model.addAttribute("nationality", nationality);
        return "authors/list";
    }
    
    // Most prolific authors
    @GetMapping("/top")
    public String topAuthors(@RequestParam(defaultValue = "10") int limit, ModelMap model) {
        List<Author> authors = authorService.findMostProlificAuthors(limit);
        model.addAttribute("authors", authors);
        model.addAttribute("pageTitle", "Top " + limit + " Most Prolific Authors");
        return "authors/list";
    }
}
