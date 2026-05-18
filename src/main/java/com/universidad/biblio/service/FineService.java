package com.universidad.biblio.service;

import com.universidad.biblio.model.Fine;
import com.universidad.biblio.model.Loan;
import com.universidad.biblio.repository.FineRepository;
import com.universidad.biblio.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FineService {
    private final FineRepository fineRepository;
    private final LoanRepository loanRepository;

    public FineService(FineRepository fineRepository, LoanRepository loanRepository) {
        this.fineRepository = fineRepository;
        this.loanRepository = loanRepository;
    }

    public List<Fine> list() {
        return fineRepository.findAll();
    }

    public List<Fine> byUser(int userId) {
        return fineRepository.findByUserId(userId);
    }

    public Fine find(Long id) {
        return fineRepository.findById(id).orElseThrow(() -> new RuntimeException("Multa no encontrada"));
    }

    public Fine generate(Long loanId, double amount) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Prestamo no encontrado"));
        return fineRepository.findByLoanIdAndStatus(loanId, "PENDIENTE")
                .orElseGet(() -> fineRepository.save(new Fine(amount, new Date(), "PENDIENTE", loan, loan.getUser())));
    }

    public Fine pay(Long id) {
        Fine fine = find(id);
        fine.setStatus("PAGADA");
        return fineRepository.save(fine);
    }
}
