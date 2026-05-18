package com.universidad.biblio.repository;

import com.universidad.biblio.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByNotificationDateDesc(int userId);
}
