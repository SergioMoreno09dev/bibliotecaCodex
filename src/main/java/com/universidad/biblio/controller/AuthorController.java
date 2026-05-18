package com.universidad.biblio.controller;

import com.universidad.biblio.model.Author;
import com.universidad.biblio.service.AuthorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {
    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    @GetMapping
    public List<Author> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public Author find(@PathVariable Long id) {
        return service.find(id);
    }

    @PostMapping
    public Author save(@RequestBody Author author) {
        return service.save(author);
    }

    @PutMapping("/{id}")
    public Author update(@PathVariable Long id, @RequestBody Author author) {
        return service.update(id, author);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
