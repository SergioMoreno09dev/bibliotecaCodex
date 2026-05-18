package com.universidad.biblio.repository;

import com.universidad.biblio.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByTypeIgnoreCase(String type);
}
