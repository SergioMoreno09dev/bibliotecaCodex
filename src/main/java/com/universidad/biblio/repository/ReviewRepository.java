package com.universidad.biblio.repository;

import com.universidad.biblio.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookIsbn(String isbn);

    List<Review> findByUserId(int userId);
}
