package ru.practicum.ewm.dto.user;

import ru.practicum.ewm.model.user.User;

public class UserMapper {

    public static UserDto toUserDto(User user){
        return UserDto.builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
