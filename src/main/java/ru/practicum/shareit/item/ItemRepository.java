package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select item from Item item" +
            " where item.available = true " +
            " and (lower(item.name) like ?1" +
            " or lower(item.description) like ?1)")
    Collection<Item> searchItems(String text);

    Collection<Item> findAllByOwnerId(long userId);
}

