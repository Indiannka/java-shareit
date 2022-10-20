package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.UserDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.converter.UserConverter;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImp serviceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    private final User user = User.builder().id(1L).name("u").email("user@email.ru").build();

    @Test
    void getByIdTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        serviceImpl.getById(1L);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getByIdThrowsNotFoundEx() {
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.getById(10L));
        assertEquals("Пользователь № 10 не найден", thrown.getMessage());
    }

    @Test
    void createUserTest() {
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(userConverter.convert(any(UserDto.class))).thenReturn(new User());
        serviceImpl.create(UserDto.builder().id(1L).name("user").email("user@email.ru").build());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void createUserFailTest() {
        UserDto userDto = UserDto.builder().build();
        when(userConverter.convert(userDto)).thenReturn(null);
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.create(userDto));
        assertEquals("Отсутствуют параметры входящего объекта userDto " + userDto, thrown.getMessage());
    }

    @Test
    void updateUserTest() {
        UserDto userDto = UserDto.builder().name("user").email("user@email.ru").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        serviceImpl.update(1L, userDto);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void deleteUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        serviceImpl.delete(user.getId());
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        serviceImpl.getAllUsers();
        verify(userRepository,times(1)).findAll();
    }
}