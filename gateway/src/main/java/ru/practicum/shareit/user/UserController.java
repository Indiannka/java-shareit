package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.dto.UserDto;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userCLient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userCLient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable("userId") long userId) {
        log.info("GET request: запрос пользователя с id {}", userId);
        return userCLient.getById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class})
                                         @RequestBody UserDto userDto) {
        log.info("POST request: создание пользователя {}", userDto.toString());
        return userCLient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public  ResponseEntity<Object>  update(@Validated({Update.class})
                          @PathVariable ("userId") long userId,
                          @RequestBody UserDto userDto) {
        log.info("PATCH request: обновление пользователя {}, id {}", userDto.toString(), userId);
        return userCLient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable("userId") long userId) {
        return userCLient.delete(userId);
    }
}