package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.item.converter.CommentConverter;
import ru.practicum.shareit.item.converter.ItemConverter;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;

import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final CommentConverter commentConverter;
    private final ItemConverter itemConverter;
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemWithBookings> get(@RequestHeader(USER_ID_HEADER) long userId,
                                            @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
                                            @RequestParam(defaultValue = "10", required = false) @Min(1) int size) {
        log.info("GET request: запрос всех предметов пользователя {}", userId);
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookings getById(@RequestHeader(USER_ID_HEADER) long userId,
                                    @PathVariable("itemId") long itemId) {
        log.info("GET request: запрос предмета id {}", itemId);
        return itemService.getById(itemId, userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                          @Validated({Create.class})
                          @RequestBody ItemDto itemDto) {
        log.info("POST request: добавление предмета {} пользователем с id {}", itemDto, userId);
        return itemConverter.convert(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) Long userId,
                          @PathVariable ("itemId") long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("PATCH request: обновление предмета {} пользователем с id {}", itemDto, userId);
        return itemConverter.convert(itemService.update(userId,itemId,itemDto));
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text,
                                           @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
                                           @RequestParam(defaultValue = "10", required = false) @Min(1) int size) {
        log.info("GET request: поиск предметов по запросу {}", text);
        return itemService.searchItems(text,from,size).stream()
                .map(itemConverter::convert)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @PathVariable ("itemId") long itemId,
                                 @Validated({Create.class})
                                 @RequestBody CommentDto commentDTO) {
        log.info("POST request: добавление комментария пользователем с id {} к предмету {}", userId, itemId);
        return commentConverter.convert(itemService.addComment(commentDTO, userId, itemId));
    }
}