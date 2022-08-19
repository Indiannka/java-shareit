package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable("userId") long userId) {
        log.info("GET request: запрос пользователя с id {}", userId);
        return userService.getById(userId);
    }

    @PostMapping
    public UserDto create(@Validated({Create.class})
                          @RequestBody UserDto userDto) {
        log.info("POST request: создание пользователя {}", userDto.toString());
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated({Update.class})
                          @PathVariable ("userId") long userId,
                          @RequestBody UserDto userDto) {
        log.info("PATCH request: обновление пользователя {}, id {}", userDto.toString(), userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") long userId) {
        userService.delete(userId);
    }
}