package com.universidad.biblio.controller;

import com.universidad.biblio.model.Loan;
import com.universidad.biblio.model.loanHistory;
import com.universidad.biblio.service.LoanService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @GetMapping
    public List<Loan> list(@RequestParam(required = false) Integer userId) {
        if (userId != null) {
            return service.byUser(userId);
        }
        return service.list();
    }

    @GetMapping("/{id}")
    public Loan find(@PathVariable Long id) {
        return service.find(id);
    }

    @PostMapping
    public Loan create(@RequestBody LoanRequest request) {
        return service.create(request.userId(), request.isbn(), request.returnDate());
    }

    @PatchMapping("/{id}/close")
    public Loan close(@PathVariable Long id) {
        return service.close(id);
    }

    @PatchMapping("/{id}/extend")
    public Loan extend(@PathVariable Long id, @RequestBody ExtendLoanRequest request) {
        return service.extend(id, request.returnDate());
    }

    @PatchMapping("/mark-expired")
    public void markExpired() {
        service.markExpiredLoans();
    }

    @GetMapping("/{id}/history")
    public List<loanHistory> history(@PathVariable Long id) {
        return service.history(id);
    }

    public record LoanRequest(int userId, String isbn, Date returnDate) {
    }

    public record ExtendLoanRequest(Date returnDate) {
    }
}
