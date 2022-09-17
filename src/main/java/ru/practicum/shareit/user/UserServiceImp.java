package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.converter.UserDtoToUserConverter;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UserDtoToUserConverter userDtoToUserConverter;

    @Override
    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
    }

    @Override
    public User create(UserDto userDto) {
        User user = userDtoToUserConverter.convert(userDto);
        if (user == null) {
            throw new NotFoundException(String.format(
                    "Отсутствуют параметры входящего объекта userDto %s ", userDto));
        }
        return userRepository.save(user);
    }

    @Override
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
    public void delete(long userId) {
        userRepository.delete(getById(userId));
    }

    @Override
    public Collection<User> getAllUsers() {
        return List.copyOf(userRepository.findAll());
    }
}