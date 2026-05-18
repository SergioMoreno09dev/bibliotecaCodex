package com.universidad.biblio.service;

import com.universidad.biblio.model.AuditLog;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.AuditLogRepository;
import com.universidad.biblio.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public List<AuditLog> list() {
        return auditLogRepository.findAll();
    }

    public List<AuditLog> byUser(int userId) {
        return auditLogRepository.findByUserId(userId);
    }

    public AuditLog add(String action, String description, Integer userId) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }
        return auditLogRepository.save(new AuditLog(action, new Date(), description, user));
    }
}
