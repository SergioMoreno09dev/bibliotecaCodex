package com.universidad.biblio.controller;

import com.universidad.biblio.model.Notification;
import com.universidad.biblio.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<Notification> list(@RequestParam(required = false) Integer userId) {
        if (userId != null) {
            return service.byUser(userId);
        }
        return service.list();
    }

    @PostMapping
    public Notification send(@RequestBody NotificationRequest request) {
        return service.send(request.userId(), request.message(), request.type());
    }

    @PatchMapping("/{id}/read")
    public Notification markAsRead(@PathVariable Long id) {
        return service.markAsRead(id);
    }

    public record NotificationRequest(int userId, String message, String type) {
    }
}
