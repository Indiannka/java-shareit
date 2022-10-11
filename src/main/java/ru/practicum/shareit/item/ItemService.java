package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;

import java.util.Collection;

public interface ItemService {

    Item create(Long userId, ItemDto itemDto);

    Item update(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemWithBookings> getItems(Long userId, int from, int size);

    ItemWithBookings getById(Long itemId, Long userId);

    Item getById(Long itemId);

    Collection<Item> searchItems(String text, int from, int size);

    Comment addComment(CommentDto commentDTO, Long userId, Long itemId);
}
