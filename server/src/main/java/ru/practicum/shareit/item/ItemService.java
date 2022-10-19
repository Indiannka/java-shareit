package ru.practicum.shareit.item;

import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    Item create(Long userId, ItemDto itemDto);

    Item update(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemWithBookings> getItems(Long userId, int from, int size, String[] sortBy);

    ItemWithBookings getById(Long itemId, Long userId);

    Item getById(Long itemId);

    Collection<Item> searchItems(String text, int from, int size);

    Comment addComment(CommentDto commentDTO, Long userId, Long itemId);
}
