package ru.practicum.shareit.item.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;

@Component
@RequiredArgsConstructor
public class ItemConverter {
    public Item convert(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemDto convert(Item item) {
        ItemDto itemDto;
        itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getRequest() != null) {
            itemDto = itemDto.toBuilder().requestId(item.getRequest().getId()).build();
        }
        return itemDto;
    }

    public ItemWithBookings convertToItemWithBookings(Item item) {
        return ItemWithBookings.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .build();
    }
}
