package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final ConversionService conversionService;
    private final UserRepository userRepository;

    @Override
    public User getById(long userId) {
        return userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
    }

    @Override
    public User create(UserDto userDto) {
        isExistEmail(userDto.getEmail());
        User user = conversionService.convert(userDto, User.class);
        return userRepository.create(user);
    }

    @Override
    public User update(Long userId, UserDto userDto) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        if (userDto.getEmail() != null) {
            isExistEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return userRepository.update(user);
    }

    @Override
    public void delete(long userId) {
        userRepository.delete(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return List.copyOf(userRepository.getAllUsers());
    }

    private void isExistEmail(String email) {
        Boolean findEmail = userRepository.getAllUsers().stream()
                .map(User::getEmail)
                .anyMatch(Predicate.isEqual(email));
        if (Boolean.TRUE.equals(findEmail)) {
            throw new EmailExistsException("Такой email уже существует: " + email);
        }
    }
}