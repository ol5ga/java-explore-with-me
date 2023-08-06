package ru.practicum.ewm.controller.category;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@AllArgsConstructor
public class CategoryAdminController {

    private CategoryService service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Category addCategory(@Valid @RequestBody NewCategoryDto name) {

        return service.addCategory(name);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long id) {
        service.deleteCategory(id);
    }

    @PatchMapping("/{id}")
    public Category updateCategory(@PathVariable long id, @Valid @RequestBody NewCategoryDto name) {
        return service.updateCategory(id, name);
    }
}
