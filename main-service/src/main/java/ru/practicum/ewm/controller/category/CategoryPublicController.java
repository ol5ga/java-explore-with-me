package ru.practicum.ewm.controller.category;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@AllArgsConstructor
public class CategoryPublicController {

    private CategoryService service;
    @GetMapping
    public List<Category> getAllCategories(@Valid @RequestParam(defaultValue = "0") Integer from,
                                           @Valid @RequestParam(defaultValue = "10") Integer size){
        return service.getAllCategories(from,size);
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable long id){
        return service.getCategory(id);
    }
}
