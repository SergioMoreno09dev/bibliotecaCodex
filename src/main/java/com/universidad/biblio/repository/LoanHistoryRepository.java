package com.universidad.biblio.repository;

import com.universidad.biblio.model.loanHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanHistoryRepository extends JpaRepository<loanHistory, Long> {
    List<loanHistory> findByLoanId(Long loanId);
}
