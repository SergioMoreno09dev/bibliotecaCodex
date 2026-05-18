package com.universidad.biblio.service;

import com.universidad.biblio.model.Permission;
import com.universidad.biblio.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {
    private final PermissionRepository repository;

    public PermissionService(PermissionRepository repository) {
        this.repository = repository;
    }

    public List<Permission> list() {
        return repository.findAll();
    }

    public List<Permission> byRole(String role) {
        return repository.findByRole(role);
    }

    public Permission save(Permission permission) {
        return repository.save(permission);
    }

    public Permission find(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Permiso no encontrado"));
    }

    public void delete(Long id) {
        repository.delete(find(id));
    }

    public boolean checkPermission(String role, String permissionName) {
        return repository.findByRole(role).stream()
                .anyMatch(permission -> permission.getName().equalsIgnoreCase(permissionName));
    }
}
