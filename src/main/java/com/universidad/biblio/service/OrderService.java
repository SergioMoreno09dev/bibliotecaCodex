package com.universidad.biblio.service;

import com.universidad.biblio.model.Book;
import com.universidad.biblio.model.Order;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.BookRepository;
import com.universidad.biblio.repository.OrderRepository;
import com.universidad.biblio.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public List<Order> list() {
        return orderRepository.findAll();
    }

    public List<Order> byUser(int userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order find(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    public Order create(int userId, String isbn) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Book book = bookRepository.findById(isbn).orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        if (book.getStock() > 0) {
            throw new RuntimeException("Solo se puede reservar un libro sin stock disponible");
        }
        Date now = new Date();
        Date expiration = Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return orderRepository.save(new Order(now, "PENDIENTE", expiration, user, book));
    }

    public Order cancel(Long id) {
        Order order = find(id);
        order.setStatus("CANCELADA");
        return orderRepository.save(order);
    }

    public Order approve(Long id) {
        Order order = find(id);
        order.setStatus("APROBADA");
        return orderRepository.save(order);
    }
}
