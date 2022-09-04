package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImp implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private static long id = 0;

    @Override
    public Item create(Item item) {
        item.setId(generateId());
        items.put(item.getId(),item);
        return items.get(item.getId());
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(),item);
        return items.get(item.getId());
    }

    @Override
    public void delete(long userId, long itemId) {
        Item item = items.get(itemId);
        if (item != null && item.getOwner().getId().equals(userId)) {
            items.remove(itemId);
        }
    }

    @Override
    public Collection<Item> getItems(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getById(long itemId) {
        return items.values().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst();
    }

    @Override
    public Collection<Item> searchItems(String text) {
        return items.values().stream()
                .filter(item -> item.getAvailable().equals(Boolean.TRUE))
                .filter(item -> item.getDescription().toLowerCase().contains(text)
                             || item.getName().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    private long generateId() {
        return ++id;
    }
}
