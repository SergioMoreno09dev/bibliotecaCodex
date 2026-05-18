package com.universidad.biblio.repository;

import com.universidad.biblio.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(int userId);

    List<Order> findByStatus(String status);

    boolean existsByBookIsbnAndStatusIn(String isbn, Collection<String> statuses);
}
