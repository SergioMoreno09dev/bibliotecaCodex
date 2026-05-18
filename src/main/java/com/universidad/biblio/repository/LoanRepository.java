package com.universidad.biblio.repository;

import com.universidad.biblio.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(int userId);

    List<Loan> findByBookIsbn(String isbn);

    boolean existsByBookIsbn(String isbn);

    List<Loan> findByStatus(String status);

    long countByStatus(String status);

    long countByReturnDateBeforeAndStatus(Date date, String status);
}
