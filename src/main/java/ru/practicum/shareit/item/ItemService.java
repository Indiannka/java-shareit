package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, long itemId, ItemDto itemDto);

    void delete(long userId, long itemId);

    Collection<ItemDto> getItems(long userId);

    ItemDto getById(long itemId);

    Collection<ItemDto> searchItems(String text);
}
