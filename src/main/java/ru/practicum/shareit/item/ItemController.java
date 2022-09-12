package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;

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

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public Collection<ItemWithBookings> get(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("GET request: запрос всех предметов пользователя {}", userId);
        return itemService.getItems(userId);
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
        return conversionService.convert(itemService.create(userId, itemDto), ItemDto.class);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) Long userId,
                          @Validated({Update.class})
                          @PathVariable ("itemId") long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("PATCH request: обновление предмета {} пользователем с id {}", itemDto, userId);
        return conversionService.convert(itemService.update(userId,itemId,itemDto), ItemDto.class);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET request: поиск предметов по запросу {}", text);
        return itemService.searchItems(text).stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @PathVariable ("itemId") long itemId,
                                 @Validated({Create.class})
                                 @RequestBody CommentDTO commentDTO) {
        log.info("POST request: добавление комментария пользователем с id {} к предмету {}", userId, itemId);
        return conversionService.convert(itemService.addComment(commentDTO, userId, itemId), CommentDTO.class);
    }
}