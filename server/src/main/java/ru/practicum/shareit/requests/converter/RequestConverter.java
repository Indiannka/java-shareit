package ru.practicum.shareit.requests.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.converter.ItemConverter;
import ru.practicum.shareit.requests.ItemRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RequestConverter {
    private final ItemRepository itemRepository;
    private final ItemConverter itemConverter;

    public ItemRequest convert(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public ItemRequestDto convert(ItemRequest itemRequest) {
        Set<ItemDto> itemDtoSet = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                .map(itemConverter::convert)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemDtoSet)
                .build();
    }
}