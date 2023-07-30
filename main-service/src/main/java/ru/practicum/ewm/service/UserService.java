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
//        if (from < 0 || size < 0) {
//            throw new IllegalArgumentException("Запрос составлен некорректно");
//        }
        List<User> users = new ArrayList<>();
        if (ids == null) {
            users = repository.findAll(PageRequest.of(from / size, size)).toList();
        } else {
            users = repository.findAllById(ids);
        }
        return users.stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());

    }

    public void deleteUser(long id) {
        User user = repository.findById(id).orElseThrow(() -> new StorageException("Пользователь не найден или недоступен"));
        repository.deleteById(id);
    }
}
