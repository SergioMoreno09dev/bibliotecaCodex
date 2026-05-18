package com.universidad.biblio.controller;

import com.universidad.biblio.model.AuditLog;
import com.universidad.biblio.service.AuditLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {
    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<AuditLog> list(@RequestParam(required = false) Integer userId) {
        if (userId != null) {
            return service.byUser(userId);
        }
        return service.list();
    }

    @PostMapping
    public AuditLog add(@RequestBody AuditLogRequest request) {
        return service.add(request.action(), request.description(), request.userId());
    }

    public record AuditLogRequest(String action, String description, Integer userId) {
    }
}
