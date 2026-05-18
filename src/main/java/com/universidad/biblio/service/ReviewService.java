package com.universidad.biblio.service;

import com.universidad.biblio.model.Book;
import com.universidad.biblio.model.Review;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.BookRepository;
import com.universidad.biblio.repository.ReviewRepository;
import com.universidad.biblio.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public List<Review> list() {
        return reviewRepository.findAll();
    }

    public List<Review> byBook(String isbn) {
        return reviewRepository.findByBookIsbn(isbn);
    }

    public Review find(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Resena no encontrada"));
    }

    public Review create(int userId, String isbn, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("La calificacion debe estar entre 1 y 5");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Book book = bookRepository.findById(isbn).orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        return reviewRepository.save(new Review(rating, comment, new Date(), user, book));
    }

    public Review update(Long id, Review review) {
        Review current = find(id);
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new RuntimeException("La calificacion debe estar entre 1 y 5");
        }
        current.setRating(review.getRating());
        current.setComment(review.getComment());
        return reviewRepository.save(current);
    }

    public void delete(Long id) {
        reviewRepository.delete(find(id));
    }
}
