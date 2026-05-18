package com.universidad.biblio.controller;

import com.universidad.biblio.model.Book;
import com.universidad.biblio.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<Book> list(@RequestParam(required = false) String term,
                           @RequestParam(required = false) String category) {
        return service.list(term, category);
    }

    @GetMapping("/{isbn}")
    public Book find(@PathVariable String isbn) {
        return service.find(isbn);
    }

    @PostMapping
    public Book save(@RequestBody Book book) {
        return service.save(book);
    }

    @PutMapping("/{isbn}")
    public Book update(@PathVariable String isbn, @RequestBody Book book) {
        return service.update(isbn, book);
    }

    @PatchMapping("/{isbn}/stock")
    public Book changeStock(@PathVariable String isbn, @RequestParam int amount) {
        return service.changeStock(isbn, amount);
    }

    @DeleteMapping("/{isbn}")
    public void delete(@PathVariable String isbn) {
        service.delete(isbn);
    }
}
