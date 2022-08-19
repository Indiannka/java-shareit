package ru.practicum.shareit.user.converter;

import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Component
@AllArgsConstructor
public class UserToUserDtoConverter implements Converter<User, UserDto> {
    @Override
    public UserDto convert(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}