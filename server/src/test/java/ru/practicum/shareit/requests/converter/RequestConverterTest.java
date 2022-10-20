package ru.practicum.shareit.requests.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.converter.ItemConverter;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestConverterTest {

    @InjectMocks
    private RequestConverter requestConverter;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemConverter converter;

    @Test
    void convertToItemRequestDto() {
        User owner = User.builder().id(1L).name("owner").email("owner@email.ru").build();
        Item item = Item.builder().id(3L).description("iDescription").owner(owner).build();
        ItemDto itemDto = ItemDto.builder().id(3L).build();
        ItemRequest itemRequest = ItemRequest.builder().id(4L).description("req")
                .created(LocalDateTime.now()).build();

        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.singletonList(item));
        when(converter.convert(item)).thenReturn(itemDto);
        ItemRequestDto itemRequestDto = requestConverter.convert(itemRequest);

        assertNotNull(itemRequestDto);
        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
    }
}