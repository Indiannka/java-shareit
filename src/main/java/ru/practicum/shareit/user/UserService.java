package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto getById(long userId);

    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    void delete(long userId);

    Collection<UserDto> getAllUsers();
}
