package ru.practicum.shareit.item.converter;

import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
@AllArgsConstructor
public class ItemDtoToItemConverter implements Converter<ItemDto, Item> {
    @Override
    public Item convert(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
