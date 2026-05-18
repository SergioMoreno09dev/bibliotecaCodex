package com.universidad.biblio.controller;

import com.universidad.biblio.model.Order;
import com.universidad.biblio.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<Order> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public Order find(@PathVariable Long id) {
        return service.find(id);
    }

    @PostMapping
    public Order create(@RequestBody OrderRequest request) {
        return service.create(request.userId(), request.isbn());
    }

    @PatchMapping("/{id}/cancel")
    public Order cancel(@PathVariable Long id) {
        return service.cancel(id);
    }

    @PatchMapping("/{id}/approve")
    public Order approve(@PathVariable Long id) {
        return service.approve(id);
    }

    public record OrderRequest(int userId, String isbn) {
    }
}
