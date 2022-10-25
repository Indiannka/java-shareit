package ru.practicum.shareit.user.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.dto.UserDto;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
class UserConverterTest {

    @InjectMocks
    private UserConverter userConverter;

    private final User user = User.builder().id(1L).name("u").email("user@email.ru").build();
    private final UserDto userDto = UserDto.builder().id(1L).name("userDto").email("userDto@email.ru").build();

    @Test
    void convertToUserTest() {
        User newUser = userConverter.convert(userDto);
        assertEquals(newUser.getId(), userDto.getId());
        assertEquals(newUser.getName(), userDto.getName());
        assertEquals(newUser.getEmail(), userDto.getEmail());
    }

    @Test
    void convertToUserDtoTest() {
        UserDto newUserDto = userConverter.convert(user);
        assertEquals(newUserDto.getId(), user.getId());
        assertEquals(newUserDto.getName(), user.getName());
        assertEquals(newUserDto.getEmail(), user.getEmail());
    }
}