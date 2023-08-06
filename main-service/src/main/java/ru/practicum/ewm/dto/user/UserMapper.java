package ru.practicum.ewm.dto.user;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.user.User;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
