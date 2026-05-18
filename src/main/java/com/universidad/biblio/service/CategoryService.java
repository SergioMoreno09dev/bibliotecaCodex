package com.universidad.biblio.service;

import com.universidad.biblio.model.Category;
import com.universidad.biblio.repository.BookRepository;
import com.universidad.biblio.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repository;
    private final BookRepository bookRepository;

    public CategoryService(CategoryRepository repository, BookRepository bookRepository) {
        this.repository = repository;
        this.bookRepository = bookRepository;
    }

    public List<Category> list() {
        return repository.findAll();
    }

    public Category find(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
    }

    public Category save(Category category) {
        return repository.save(category);
    }

    public Category update(Long id, Category category) {
        Category current = find(id);
        current.setName(category.getName());
        current.setDescription(category.getDescription());
        return repository.save(current);
    }

    public void delete(Long id) {
        Category category = find(id);
        if (bookRepository.existsByCategoryId(id)) {
            throw new RuntimeException("No se puede eliminar la categoria porque tiene libros asociados");
        }
        repository.delete(category);
    }
}
