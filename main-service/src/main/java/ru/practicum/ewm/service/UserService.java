package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.user.UserResponse;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.user.UserRepository;

import javax.validation.ConstraintViolationException;

@Service
@Data
@AllArgsConstructor
public class UserService {

    private UserRepository repository;
    private ModelMapper mapper;

    public UserDto addUser(UserResponse userResponse) {
        if(userResponse.getName() == null || userResponse.getEmail() == null){
            throw new ValidationException("Запрос составлен некорректно");
        }
        User user;
        try {
            user = repository.save(mapper.map(userResponse,User.class));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Нарушение целостности данных");
        }
        return   UserDto.builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
