package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    Optional<User> getById(long userId);

    Collection<User> getAllUsers();

    User create(User user);

    User update(User user);

    void delete(long userId);
}
