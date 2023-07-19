package ru.practicum.ewm.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryRequest;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@Builder
public class CategoryService {

    private CategoryRepository repository;
    public Category addCategory(CategoryRequest name) {
        Category category = Category.builder()
                .name(name.getName())
                .build();
        return repository.save(category);
    }

    public void deleteCategory(long id) {
        Category category = repository.findById(id).orElseThrow(() -> new StorageException("Категория не найдена или недоступна"));

        //TODO
//        if(coptilatationRepository.findCategory(id){
//        throw new ConflictException(""Существуют события, связанные с категорией)
//
        repository.delete(category);
    }

    public Category updateCategory(long id, CategoryRequest name) {
        Category category = repository.findById(id).orElseThrow(() -> new StorageException("Категория не найдена или недоступна"));
        //TODO
//        if(coptilatationRepository.findCategory(id){
//        throw new ConflictException("Нарушение целостности данных")
//
        if (name.getName() != null){
            category.setName(name.getName());
        }
        repository.save(category);
        return repository.findById(category.getId()).orElseThrow();
    }

    public List<Category> getAllCategories(Integer from, Integer size) {
        List<Category> all = new ArrayList<>();
        if (from < 0 || size < 0) {
            throw new IllegalArgumentException("Запрос составлен некорректно");
        }
        all = repository.findAll(PageRequest.of(from/size, size)).toList();
        return all;
    }

    public Category getCategory(long id) {
        if (id<0){
            throw new IllegalArgumentException("Запрос составлен некорректно");
        }
        return repository.findById(id).orElseThrow(()-> new StorageException("Категория не найдена или недоступна"));
    }
}
