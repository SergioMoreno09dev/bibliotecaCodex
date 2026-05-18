package com.universidad.biblio.controller;

import com.universidad.biblio.model.Permission;
import com.universidad.biblio.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    private final PermissionService service;

    public PermissionController(PermissionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Permission> list(@RequestParam(required = false) String role) {
        if (role != null && !role.isBlank()) {
            return service.byRole(role);
        }
        return service.list();
    }

    @PostMapping
    public Permission save(@RequestBody Permission permission) {
        return service.save(permission);
    }

    @GetMapping("/check")
    public Map<String, Boolean> check(@RequestParam String role, @RequestParam String permission) {
        return Map.of("allowed", service.checkPermission(role, permission));
    }
}
