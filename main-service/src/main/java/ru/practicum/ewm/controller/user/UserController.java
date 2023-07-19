package ru.practicum.ewm.controller.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.user.UserResponse;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@AllArgsConstructor
public class UserController {
    private UserService service;

    @GetMapping
    public List<UserDto> getUser(@RequestParam List<Long> ids,
                        @RequestParam(defaultValue = "0") Integer from,
                        @RequestParam(defaultValue = "10") Integer size){
        return service.getUsers(ids,from,size);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDto addUser(@RequestBody UserResponse userResponse){
        return service.addUser(userResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long id){
        service.deleteUser(id);
    }
}

