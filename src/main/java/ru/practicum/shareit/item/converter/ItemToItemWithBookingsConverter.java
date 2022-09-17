package ru.practicum.shareit.item.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemWithBookings;

@Component
@AllArgsConstructor
public class ItemToItemWithBookingsConverter {

    public ItemWithBookings convert(Item item) {
        return ItemWithBookings.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .build();
    }
}
