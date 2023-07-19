package ru.practicum.ewm.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryRequest;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;

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
}
