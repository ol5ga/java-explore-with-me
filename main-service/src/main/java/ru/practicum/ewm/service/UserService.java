package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.user.UserResponse;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.user.UserRepository;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@AllArgsConstructor
public class UserService {

    private UserRepository repository;
    private ModelMapper mapper;

    public UserDto addUser(UserResponse userResponse) {
        if(userResponse.getName() == null || userResponse.getEmail() == null ||
                userResponse.getName().isBlank() || userResponse.getEmail().isBlank() ||
                userResponse.getName().length()<2 || userResponse.getName().length()>250 ||
                userResponse.getEmail().length()<6 || userResponse.getEmail().length()>254){
            throw new ValidationException("Запрос составлен некорректно");
        }
        User user;
        try {
            user = repository.save(mapper.map(userResponse,User.class));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Нарушение целостности данных");
        }
        return toUserDto(user);
    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new IllegalArgumentException("Запрос составлен некорректно");
        }
        List<User> users = new ArrayList<>();
        if (ids == null){
            users = repository.findAll(PageRequest.of(from/size, size)).toList();
        } else {
            users = repository.findAllById(ids);
        }
        return users.stream()
                .map(user -> toUserDto(user))
                .collect(Collectors.toList());

    }

    private UserDto toUserDto(User user){
        return UserDto.builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public void deleteUser(long id) {
        User user = repository.findById(id).orElseThrow(()-> new StorageException("Пользователь не найден или недоступен"));
            repository.deleteById(id);
    }
}
