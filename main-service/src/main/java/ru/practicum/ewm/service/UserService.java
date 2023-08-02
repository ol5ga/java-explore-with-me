package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserMapper;
import ru.practicum.ewm.dto.user.UserRequest;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@AllArgsConstructor
public class UserService {

    private UserRepository repository;
    private ModelMapper mapper;

    public UserDto addUser(UserRequest userRequest) {
        User user;
        try {
            user = repository.save(mapper.map(userRequest, User.class));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Нарушение целостности данных");
        }
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        if (ids == null) {
            users = repository.findAll(PageRequest.of(from / size, size)).toList();
        } else {
            users = repository.findAllById(ids);
        }
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

    }

    public void deleteUser(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new StorageException("Пользователь не найден или недоступен");
        }
    }
}
