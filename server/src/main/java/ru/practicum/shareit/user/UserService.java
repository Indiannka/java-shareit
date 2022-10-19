package ru.practicum.shareit.user;


import ru.practicum.shareit.dto.UserDto;

import java.util.Collection;

public interface UserService {
    User getById(long userId);

    User create(UserDto userDto);

    User update(Long userId, UserDto userDto);

    void delete(long userId);

    Collection<User> getAllUsers();
}
