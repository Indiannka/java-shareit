package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ConversionService conversionService;
    private final ItemService itemService;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public Collection<ItemDto> get(@RequestHeader(HEADER_USER_ID) long userId) {
        log.info("GET request: запрос всех предметов пользователя {}", userId);
        return itemService.getItems(userId).stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable("itemId") long itemId) {
        log.info("GET request: запрос предмета id {}", itemId);
        return conversionService.convert(itemService.getById(itemId), ItemDto.class);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER_USER_ID) Long userId,
                          @Validated({Create.class})
                          @RequestBody ItemDto itemDto) {
        log.info("POST request: добавление предмета {} пользователем с id {}", itemDto, userId);
        return conversionService.convert(itemService.create(userId, itemDto), ItemDto.class);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(HEADER_USER_ID) Long userId,
                          @Validated({Update.class})
                          @PathVariable ("itemId") long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("PATCH request: обновление предмета {} пользователем с id {}", itemDto, userId);
        return conversionService.convert(itemService.update(userId,itemId,itemDto), ItemDto.class);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(HEADER_USER_ID) long userId,
                       @PathVariable long itemId) {
        itemService.delete(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET request: поиск предметов по запросу {}", text);
        return itemService.searchItems(text).stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }
}