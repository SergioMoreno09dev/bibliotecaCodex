package com.universidad.biblio.service;

import com.universidad.biblio.model.Notification;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.NotificationRepository;
import com.universidad.biblio.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository,
                               EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public List<Notification> list() {
        return notificationRepository.findAll();
    }

    public List<Notification> byUser(int userId) {
        return notificationRepository.findByUserIdOrderByNotificationDateDesc(userId);
    }

    public Notification send(int userId, String message, String type) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Notification notification = notificationRepository.save(new Notification(message, new Date(), type, false, user));
        boolean emailSent = emailService.sendNotification(user, subject(type), message);
        if (!emailSent) {
            logger.info("Notification {} was saved for user {}, but email delivery was not completed",
                    notification.getId(), user.getId());
        }
        return notification;
    }

    private String subject(String type) {
        if (type == null || type.isBlank()) {
            return "Notificacion CodexLibrary";
        }
        return "CodexLibrary - " + type;
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificacion no encontrada"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
