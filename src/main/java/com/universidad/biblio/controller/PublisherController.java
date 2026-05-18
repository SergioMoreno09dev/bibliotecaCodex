package com.universidad.biblio.controller;

import com.universidad.biblio.model.Publisher;
import com.universidad.biblio.service.PublisherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {
    private final PublisherService service;

    public PublisherController(PublisherService service) {
        this.service = service;
    }

    @GetMapping
    public List<Publisher> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public Publisher find(@PathVariable Long id) {
        return service.find(id);
    }

    @PostMapping
    public Publisher save(@RequestBody Publisher publisher) {
        return service.save(publisher);
    }

    @PutMapping("/{id}")
    public Publisher update(@PathVariable Long id, @RequestBody Publisher publisher) {
        return service.update(id, publisher);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
