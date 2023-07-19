package ru.practicum.ewm.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryRequest;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.service.CategoryService;

@RestController
@RequestMapping(path = "/admin/categories")
@AllArgsConstructor
public class CategoryAdminController {

    private CategoryService service;

    @PostMapping
    public Category addCategory(@RequestBody CategoryRequest name){
        return service.addCategory(name);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable long id){
        service.deleteCategory(id);
    }
}
