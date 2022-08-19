package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET request: запрос всех предметов пользователя {}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable("itemId") long itemId) {
        log.info("GET request: запрос предмета id {}", itemId);
        return itemService.getById(itemId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated({Create.class})
                          @RequestBody ItemDto itemDto) {
        log.info("POST request: добавление предмета {} пользователем с id {}", itemDto, userId);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated({Update.class})
                          @PathVariable ("itemId") long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("PATCH request: обновление предмета {} пользователем с id {}", itemDto, userId);
        return itemService.update(userId,itemId,itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long itemId) {
        itemService.delete(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET request: поиск предметов по запросу {}", text);
        return itemService.searchItems(text);
    }
}