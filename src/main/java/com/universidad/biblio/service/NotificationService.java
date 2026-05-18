package com.universidad.biblio.service;

import com.universidad.biblio.model.Notification;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.NotificationRepository;
import com.universidad.biblio.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<Notification> list() {
        return notificationRepository.findAll();
    }

    public List<Notification> byUser(int userId) {
        return notificationRepository.findByUserIdOrderByNotificationDateDesc(userId);
    }

    public Notification send(int userId, String message, String type) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return notificationRepository.save(new Notification(message, new Date(), type, false, user));
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificacion no encontrada"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
