package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final ConversionService conversionService;
    private final UserRepository userRepository;

    @Override
    public UserDto getById(long userId) {
        return userRepository.getById(userId)
                .map(u -> conversionService.convert(u, UserDto.class))
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
    }

    @Override
    public UserDto create(UserDto userDto) {
        isExistEmail(userDto.getEmail());
        User user = conversionService.convert(userDto, User.class);
        return conversionService.convert(userRepository.create(user), UserDto.class);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        if (userDto.getEmail() != null) {
            isExistEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return conversionService.convert(userRepository.update(user), UserDto.class);
    }

    @Override
    public void delete(long userId) {
        userRepository.delete(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(u -> conversionService.convert(u, UserDto.class))
                .collect(Collectors.toList());
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