package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.converter.UserConverter;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Override
    @Transactional(readOnly = true)
    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
    }

    @Override
    @Transactional
    public User create(UserDto userDto) {
        User user = userConverter.convert(userDto);
        if (user == null) {
            throw new NotFoundException(String.format(
                    "Отсутствуют параметры входящего объекта userDto %s ", userDto));
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Long userId, UserDto userDto) {
        User user = getById(userId);
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(long userId) {
        userRepository.delete(getById(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> getAllUsers() {
        return List.copyOf(userRepository.findAll());
    }
}