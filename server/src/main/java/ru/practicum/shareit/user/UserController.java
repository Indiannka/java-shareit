package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.UserDto;
import ru.practicum.shareit.user.converter.UserConverter;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final UserConverter userConverter;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(userConverter::convert)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable("userId") long userId) {
        return userConverter.convert(userService.getById(userId));
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        return userConverter.convert(userService.create(userDto));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable ("userId") long userId,
                          @RequestBody UserDto userDto) {
        return userConverter.convert(userService.update(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") long userId) {
        userService.delete(userId);
    }
}