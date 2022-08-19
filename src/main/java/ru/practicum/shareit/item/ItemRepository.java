package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    Item update(Item item);

    void delete(long userId, long itemId);

    Collection<Item> getItems(long userId);

    Optional<Item> getById(long itemId);

    Collection<Item> searchItems(String text);
}
