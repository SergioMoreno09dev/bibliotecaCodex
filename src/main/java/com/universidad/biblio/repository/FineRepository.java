package com.universidad.biblio.repository;

import com.universidad.biblio.model.Fine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Long> {
    List<Fine> findByUserId(int userId);

    Optional<Fine> findByLoanIdAndStatus(Long loanId, String status);
}
