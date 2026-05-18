package com.universidad.biblio.service;

import com.universidad.biblio.model.Book;
import com.universidad.biblio.repository.BookRepository;
import com.universidad.biblio.repository.LoanRepository;
import com.universidad.biblio.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {
    private final BookRepository repository;
    private final LoanRepository loanRepository;
    private final OrderRepository orderRepository;

    public BookService(BookRepository repository, LoanRepository loanRepository, OrderRepository orderRepository) {
        this.repository = repository;
        this.loanRepository = loanRepository;
        this.orderRepository = orderRepository;
    }

    public List<Book> list(String term, String category) {
        if ((term == null || term.isBlank()) && (category == null || category.isBlank())) {
            return repository.findAll();
        }
        return repository.search(term, category);
    }

    public Book find(String isbn) {
        return repository.findById(isbn).orElseThrow(() -> new RuntimeException("Libro no encontrado"));
    }

    public Book save(Book book) {
        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new RuntimeException("El ISBN es obligatorio");
        }
        if (book.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        return repository.save(book);
    }

    public Book update(String isbn, Book book) {
        Book current = find(isbn);
        current.setTitle(book.getTitle());
        current.setAuthor(book.getAuthor());
        current.setCategory(book.getCategory());
        current.setStock(book.getStock());
        current.setLanguage(book.getLanguage());
        current.setYear(book.getYear());
        current.setType(book.getType());
        current.setCantPage(book.getCantPage());
        current.setPublisher(book.getPublisher());
        return save(current);
    }

    @Transactional
    public Book changeStock(String isbn, int amount) {
        Book book = find(isbn);
        int newStock = book.getStock() + amount;
        if (newStock < 0) {
            throw new RuntimeException("No hay stock disponible");
        }
        book.setStock(newStock);
        return repository.save(book);
    }

    public void delete(String isbn) {
        Book book = find(isbn);
        if (loanRepository.existsByBookIsbn(isbn)) {
            throw new RuntimeException("No se puede eliminar el libro porque tiene prestamos asociados");
        }
        if (orderRepository.existsByBookIsbnAndStatusIn(isbn, List.of("PENDIENTE"))) {
            throw new RuntimeException("No se puede eliminar el libro porque tiene reservas pendientes");
        }
        repository.delete(book);
    }

    public long count() {
        return repository.count();
    }
}
