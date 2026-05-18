package com.universidad.biblio.repository;

import com.universidad.biblio.model.ExportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExportRequestRepository extends JpaRepository<ExportRequest, Long> {
    List<ExportRequest> findByUserId(int userId);
}
