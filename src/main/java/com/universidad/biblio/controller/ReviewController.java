package com.universidad.biblio.controller;

import com.universidad.biblio.model.Review;
import com.universidad.biblio.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping
    public List<Review> list(@RequestParam(required = false) String isbn) {
        if (isbn != null && !isbn.isBlank()) {
            return service.byBook(isbn);
        }
        return service.list();
    }

    @PostMapping
    public Review create(@RequestBody ReviewRequest request) {
        return service.create(request.userId(), request.isbn(), request.rating(), request.comment());
    }

    @PutMapping("/{id}")
    public Review update(@PathVariable Long id, @RequestBody Review review) {
        return service.update(id, review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    public record ReviewRequest(int userId, String isbn, int rating, String comment) {
    }
}
