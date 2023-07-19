package ru.practicum.ewm.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.CategoryRequest;
import ru.practicum.ewm.model.Category;

@RestController
@RequestMapping(path = "/admin/categories")
@AllArgsConstructor
public class CategoryAdminController {

    public Category addCategory(@RequestBody CategoryRequest name){

    }
}
