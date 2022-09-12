package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;

import java.util.Collection;

public interface ItemService {

    Item create(Long userId, ItemDto itemDto);

    Item update(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemWithBookings> getItems(Long userId);

    ItemWithBookings getById(Long itemId, Long userId);

    Item getById(Long itemId);

    Collection<Item> searchItems(String text);

    Comment addComment(CommentDTO commentDTO, Long userId, Long itemId);
}
