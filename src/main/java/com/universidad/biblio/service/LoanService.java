package com.universidad.biblio.service;

import com.universidad.biblio.model.Book;
import com.universidad.biblio.model.Loan;
import com.universidad.biblio.model.User;
import com.universidad.biblio.model.loanHistory;
import com.universidad.biblio.repository.BookRepository;
import com.universidad.biblio.repository.LoanHistoryRepository;
import com.universidad.biblio.repository.LoanRepository;
import com.universidad.biblio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class LoanService {
    public static final String ACTIVE = "ACTIVO";
    public static final String RETURNED = "DEVUELTO";
    public static final String EXPIRED = "VENCIDO";

    private final LoanRepository loanRepository;
    private final LoanHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final NotificationService notificationService;

    public LoanService(LoanRepository loanRepository, LoanHistoryRepository historyRepository,
                       UserRepository userRepository, BookRepository bookRepository,
                       NotificationService notificationService) {
        this.loanRepository = loanRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.notificationService = notificationService;
    }

    public List<Loan> list() {
        return loanRepository.findAll();
    }

    public List<Loan> byUser(int userId) {
        return loanRepository.findByUserId(userId);
    }

    public Loan find(Long id) {
        return loanRepository.findById(id).orElseThrow(() -> new RuntimeException("Prestamo no encontrado"));
    }

    @Transactional
    public Loan create(int userId, String isbn, Date returnDate) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Book book = bookRepository.findById(isbn).orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        if (book.getStock() <= 0) {
            throw new RuntimeException("No hay ejemplares disponibles para prestar");
        }
        book.setStock(book.getStock() - 1);
        Loan loan = new Loan(user, book, new Date(), returnDate);
        loan.setStatus(ACTIVE);
        Loan saved = loanRepository.save(loan);
        historyRepository.save(new loanHistory("NUEVO", ACTIVE, new Date(), saved));
        notificationService.send(user.getId(),
                "Se registro un prestamo del libro \"" + book.getTitle() + "\". Fecha de devolucion: " + returnDate,
                "PRESTAMO");
        return saved;
    }

    @Transactional
    public Loan close(Long id) {
        Loan loan = find(id);
        String previous = loan.getStatus();
        if (!RETURNED.equals(previous)) {
            loan.setStatus(RETURNED);
            loan.getBook().setStock(loan.getBook().getStock() + 1);
            historyRepository.save(new loanHistory(previous, RETURNED, new Date(), loan));
            notificationService.send(loan.getUser().getId(),
                    "Tu prestamo del libro \"" + loan.getBook().getTitle() + "\" fue marcado como devuelto.",
                    "PRESTAMO");
        }
        return loanRepository.save(loan);
    }

    @Transactional
    public Loan extend(Long id, Date newReturnDate) {
        Loan loan = find(id);
        if (RETURNED.equals(loan.getStatus())) {
            throw new RuntimeException("No se puede extender un prestamo devuelto");
        }
        loan.setReturnDate(newReturnDate);
        String previous = loan.getStatus();
        loan.setStatus(ACTIVE);
        historyRepository.save(new loanHistory(previous, ACTIVE, new Date(), loan));
        notificationService.send(loan.getUser().getId(),
                "Tu prestamo del libro \"" + loan.getBook().getTitle() + "\" fue extendido. Nueva fecha de devolucion: " + newReturnDate,
                "PRESTAMO");
        return loanRepository.save(loan);
    }

    @Transactional
    public void markExpiredLoans() {
        Date today = new Date();
        for (Loan loan : loanRepository.findByStatus(ACTIVE)) {
            if (loan.getReturnDate() != null && loan.getReturnDate().before(today)) {
                loan.setStatus(EXPIRED);
                historyRepository.save(new loanHistory(ACTIVE, EXPIRED, new Date(), loan));
                loanRepository.save(loan);
                notificationService.send(loan.getUser().getId(),
                        "Tu prestamo del libro \"" + loan.getBook().getTitle() + "\" esta vencido.",
                        "VENCIMIENTO");
            }
        }
    }

    public List<loanHistory> history(Long loanId) {
        return historyRepository.findByLoanId(loanId);
    }

    public long countActive() {
        return loanRepository.countByStatus(ACTIVE);
    }

    public long countExpired() {
        return loanRepository.countByReturnDateBeforeAndStatus(new Date(), ACTIVE)
                + loanRepository.countByStatus(EXPIRED);
    }
}
