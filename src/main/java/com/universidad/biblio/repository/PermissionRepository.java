package com.universidad.biblio.repository;

import com.universidad.biblio.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByRole(String role);
}
