package com.universidad.biblio.controller;

import com.universidad.biblio.model.ExportRequest;
import com.universidad.biblio.model.Report;
import com.universidad.biblio.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping
    public List<Report> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public Report find(@PathVariable Long id) {
        return service.find(id);
    }

    @PostMapping
    public Report save(@RequestBody Report report) {
        return service.save(report);
    }

    @PostMapping("/summary")
    public Report generateSummary() {
        return service.generateSummary();
    }

    @PostMapping("/{id}/export")
    public ExportRequest export(@PathVariable Long id, @RequestBody ExportRequestBody request) {
        return service.export(id, request.userId(), request.format(), request.filters());
    }

    public record ExportRequestBody(int userId, String format, String filters) {
    }
}
