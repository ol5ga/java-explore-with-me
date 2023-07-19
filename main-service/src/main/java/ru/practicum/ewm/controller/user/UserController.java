package ru.practicum.ewm.controller.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.user.UserResponse;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.service.UserService;

@RestController
@RequestMapping(path = "/admin/users")
@AllArgsConstructor
public class UserController {
    private UserService service;

    @GetMapping
    public void getUser(){

    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDto addUser(@RequestBody UserResponse userResponse){
        return service.addUser(userResponse);
    }
}

