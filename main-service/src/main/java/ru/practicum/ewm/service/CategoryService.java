package ru.practicum.ewm.service;

import lombok.Builder;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;

@Service
@Data
@Builder
public class CategoryService {

    private CategoryRepository repository;
    private EventRepository eventRepository;
    private ModelMapper mapper;

    public Category addCategory(NewCategoryDto name) {
        Category category;
        try {
            category = repository.save(mapper.map(name, Category.class));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Нарушение целостности данных");
        }
        return category;
    }


    public void deleteCategory(long id) {
        Category category = repository.findById(id).orElseThrow(() -> new StorageException("Категория не найдена или недоступна"));

        if (eventRepository.findAllByCategory_Id(id).size() != 0) {
            throw new ConflictException("Существуют события, связанные с категорией");
        }
        repository.delete(category);
    }

    public Category updateCategory(long id, NewCategoryDto name) {
        Category category = repository.findById(id).orElseThrow(() -> new StorageException("Категория не найдена или недоступна"));
        category.setName(name.getName());
        try {
            repository.save(category);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Нарушение целостности данных");
        }
        return repository.findById(category.getId()).orElseThrow();
    }

    public List<Category> getAllCategories(Integer from, Integer size) {
        return repository.findAll(PageRequest.of(from / size, size)).toList();
    }

    public Category getCategory(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Запрос составлен некорректно");
        }
        return repository.findById(id).orElseThrow(() -> new StorageException("Категория не найдена или недоступна"));
    }
}
