package ru.practicum.shareit.item.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemConverterTest {

    @InjectMocks
    private ItemConverter itemConverter;

    private final User owner = User.builder().id(1L).name("owner").email("owner@email.ru").build();
    private final User booker = User.builder().id(6L).name("booker").email("booker@email.ru").build();
    private final ItemRequest itemRequest = ItemRequest.builder().id(4L).description("needItem").requestor(booker).build();
    private final Item item = Item.builder().id(3L).description("iDescription").owner(owner).request(itemRequest).build();
    private final ItemDto itemDto = ItemDto.builder().id(3L).available(true).description("newDescr").name("iName").build();

    @Test
    void convertToItemTest() {
        Item itemRes = itemConverter.convert(itemDto);
        assertEquals(itemRes.getId(),itemDto.getId());
        assertEquals(itemRes.getAvailable(),itemDto.getAvailable());
        assertEquals(itemRes.getDescription(),itemDto.getDescription());
        assertEquals(itemRes.getName(),itemDto.getName());
    }

    @Test
    void convertToItemDtoTest() {
        ItemDto itemDtoRes = itemConverter.convert(item);
        assertEquals(itemDtoRes.getId(),item.getId());
        assertEquals(itemDtoRes.getAvailable(),item.getAvailable());
        assertEquals(itemDtoRes.getDescription(),item.getDescription());
        assertEquals(itemDtoRes.getName(),item.getName());
        assertEquals(itemDtoRes.getRequestId(),item.getRequest().getId());
    }

    @Test
    void convertToItemWithBookingsTest() {
        ItemWithBookings itemWithBookingsres = itemConverter.convertToItemWithBookings(item);
        assertEquals(itemWithBookingsres.getId(),item.getId());
        assertEquals(itemWithBookingsres.getAvailable(),item.getAvailable());
        assertEquals(itemWithBookingsres.getDescription(),item.getDescription());
        assertEquals(itemWithBookingsres.getName(),item.getName());
    }
}