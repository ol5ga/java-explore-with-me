package ru.practicum.ewm.controller.category;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@AllArgsConstructor
@Validated
public class CategoryPublicController {

    private CategoryService service;

    @GetMapping
    public List<Category> getAllCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @Valid @RequestParam(defaultValue = "10") @Positive Integer size) {
        return service.getAllCategories(from, size);
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable long id) {
        return service.getCategory(id);
    }
}
