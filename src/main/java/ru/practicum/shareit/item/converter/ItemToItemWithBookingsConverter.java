package ru.practicum.shareit.item.converter;

import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemWithBookings;

@Component
@AllArgsConstructor
public class ItemToItemWithBookingsConverter implements Converter<Item, ItemWithBookings> {
    @Override
    public ItemWithBookings convert(Item item) {
        return ItemWithBookings.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .build();
    }
}
