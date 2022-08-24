package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final ConversionService conversionService;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(u -> conversionService.convert(u, UserDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable("userId") long userId) {
        log.info("GET request: запрос пользователя с id {}", userId);
        return conversionService.convert(userService.getById(userId), UserDto.class);
    }

    @PostMapping
    public UserDto create(@Validated({Create.class})
                          @RequestBody UserDto userDto) {
        log.info("POST request: создание пользователя {}", userDto.toString());
        return conversionService.convert(userService.create(userDto), UserDto.class);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated({Update.class})
                          @PathVariable ("userId") long userId,
                          @RequestBody UserDto userDto) {
        log.info("PATCH request: обновление пользователя {}, id {}", userDto.toString(), userId);
        return conversionService.convert(userService.update(userId, userDto), UserDto.class);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") long userId) {
        userService.delete(userId);
    }
}