package com.universidad.biblio.service;

import com.universidad.biblio.model.Author;
import com.universidad.biblio.repository.AuthorRepository;
import com.universidad.biblio.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository repository;
    private final BookRepository bookRepository;

    public AuthorService(AuthorRepository repository, BookRepository bookRepository) {
        this.repository = repository;
        this.bookRepository = bookRepository;
    }

    public List<Author> list() {
        return repository.findAll();
    }

    public Author find(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Autor no encontrado"));
    }

    public Author save(Author author) {
        return repository.save(author);
    }

    public Author update(Long id, Author author) {
        Author current = find(id);
        current.setName(author.getName());
        current.setNation(author.getNation());
        return repository.save(current);
    }

    public void delete(Long id) {
        Author author = find(id);
        if (bookRepository.existsByAuthorId(id)) {
            throw new RuntimeException("No se puede eliminar el autor porque tiene libros asociados");
        }
        repository.delete(author);
    }
}
