package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.item.converter.CommentConverter;
import ru.practicum.shareit.item.converter.ItemConverter;

import java.util.Collection;
import java.util.stream.Collectors;

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
                                            @RequestParam int from,
                                            @RequestParam int size,
                                            @RequestParam String[] sortBy) {
        return itemService.getItems(userId, from, size, sortBy);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookings getById(@RequestHeader(USER_ID_HEADER) long userId,
                                    @PathVariable("itemId") long itemId) {
        return itemService.getById(itemId, userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                          @RequestBody ItemDto itemDto) {
        return itemConverter.convert(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) Long userId,
                          @PathVariable ("itemId") long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemConverter.convert(itemService.update(userId,itemId,itemDto));
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text,
                                           @RequestParam int from,
                                           @RequestParam int size) {
        return itemService.searchItems(text,from,size).stream()
                .map(itemConverter::convert)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @PathVariable ("itemId") Long itemId,
                                 @RequestBody CommentDto commentDTO) {
        return commentConverter.convert(itemService.addComment(commentDTO, userId, itemId));
    }
}