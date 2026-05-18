package com.universidad.biblio.service;

import com.universidad.biblio.model.ExportRequest;
import com.universidad.biblio.model.Report;
import com.universidad.biblio.model.User;
import com.universidad.biblio.repository.ExportRequestRepository;
import com.universidad.biblio.repository.ReportRepository;
import com.universidad.biblio.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final ExportRequestRepository exportRequestRepository;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final LoanService loanService;

    public ReportService(ReportRepository reportRepository, ExportRequestRepository exportRequestRepository,
                         UserRepository userRepository, BookService bookService, LoanService loanService) {
        this.reportRepository = reportRepository;
        this.exportRequestRepository = exportRequestRepository;
        this.userRepository = userRepository;
        this.bookService = bookService;
        this.loanService = loanService;
    }

    public List<Report> list() {
        return reportRepository.findAll();
    }

    public Report find(Long id) {
        return reportRepository.findById(id).orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
    }

    public Report generateSummary() {
        String content = "Total libros: " + bookService.count()
                + "\nPrestamos activos: " + loanService.countActive()
                + "\nPrestamos vencidos: " + loanService.countExpired();
        return reportRepository.save(new Report("Resumen biblioteca", "RESUMEN", content));
    }

    public Report save(Report report) {
        return reportRepository.save(report);
    }

    public ExportRequest export(Long reportId, int userId, String format, String filters) {
        Report report = find(reportId);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return exportRequestRepository.save(new ExportRequest(format, new Date(), filters, user, report));
    }
}
