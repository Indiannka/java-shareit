package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private static long id = 0;

    @Override
    public Optional<User> getById(long userId) {
        return users.values().stream()
                .filter(u -> u.getId() == (userId))
                .findFirst();
    }

    @Override
    public Collection<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(generateId());
        users.put(user.getId(),user);
        return users.get(user.getId());
    }

    @Override
    public User update(User user) {
        users.put(user.getId(),user);
        return users.get(user.getId());
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

    private long generateId() {
        return ++id;
    }

}