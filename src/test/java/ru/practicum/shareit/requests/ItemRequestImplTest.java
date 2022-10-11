package ru.practicum.shareit.requests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.requests.converter.RequestConverter;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class ItemRequestImplTest {

    @InjectMocks
    private ItemRequestServiceImpl serviceImpl;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private RequestConverter requestConverter;

    private final String[] sortBy = new String[] {"created;ASC"};

    private final User requestor = User.builder().id(2L).name("requestor").email("requestorr@email.ru").build();
    private final ItemRequest itemRequest = ItemRequest.builder().id(4L).requestor(requestor).build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("smth").build();

    @Test
    void createItemRequestTest() {
        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(requestConverter.convert(any(ItemRequestDto.class))).thenReturn(itemRequest);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        serviceImpl.create(2L, itemRequestDto);

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Check create method throws NotFoundException when itemDto is empty")
    void createItemRequestFailTest() {
        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(requestConverter.convert(any(ItemRequestDto.class))).thenReturn(null);

        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.create(2L, itemRequestDto));
        assertEquals("Отсутствуют параметры входящего объекта itemRequestDto " + itemRequestDto, thrown.getMessage());
    }

    @Test
    @DisplayName("Check create method throws NotFoundException when wrong user")
    void createItemRequestThrowsNotFoundExceptionTest() {
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.create(2L, itemRequestDto));
        assertEquals("Пользователь № 2 не найден", thrown.getMessage());
    }

    @Test
    void getAllUserRequestsTest() {
        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        serviceImpl.getAllUserRequests(2L, sortBy);
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).getAllByRequestorId(anyLong(), any());
    }

    @Test
    @DisplayName("Check getAllUserRequests method throws NotFoundException when wrong user request")
    void getAllUserRequestsThrowsNotFoundExceptionTest() {
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.getAllUserRequests(4L, sortBy));
        assertEquals("Пользователь № 4 не найден", thrown.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllTest() {
        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(itemRequest)));
        int page = 0;
        int size = 10;
        serviceImpl.getAll(2L, page, size, sortBy);
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Check getAllUserRequests method throws NotFoundException when wrong user request")
    void getAllThrowsNotFoundExceptionTest() {
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.getAll(4L,0,10, sortBy));
        assertEquals("Пользователь № 4 не найден", thrown.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getByIdTest() {
        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        serviceImpl.getById(4L, 2L);
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Check getById method throws NotFoundException when wrong user request")
    void getByIdItemRequestThrowsNotFoundExceptionTest() {
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.getById(4L, 2L));
        assertEquals("Пользователь № 2 не найден", thrown.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Check getById method throws NotFoundException when wrong item request")
    void getByIdItemRequestThrowsNotFoundExceptionForItemTest() {
        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.getById(4L, requestor.getId()));
        assertEquals("Запрос № 4 не найден", thrown.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }
}