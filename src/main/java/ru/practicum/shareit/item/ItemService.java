package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    Item create(Long userId, ItemDto itemDto);

    Item update(Long userId, long itemId, ItemDto itemDto);

    void delete(long userId, long itemId);

    Collection<Item> getItems(long userId);

    Item getById(long itemId);

    Collection<Item> searchItems(String text);
}
