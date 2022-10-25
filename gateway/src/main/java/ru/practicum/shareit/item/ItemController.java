package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;

import javax.validation.constraints.Min;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID_HEADER) long userId,
                                               @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
                                               @RequestParam(defaultValue = "10", required = false) @Min(1) int size,
                                               @RequestParam(defaultValue = "id;ASC", required = false) String[] sortBy) {
        log.info("GET request: запрос всех предметов пользователя {}, в порядке {}", userId, sortBy);
        return itemClient.getItems(userId, from, size, sortBy);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable("itemId") long itemId) {
        log.info("GET request: запрос предмета id {}", itemId);
        return itemClient.getById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @Validated({Create.class})
                                         @RequestBody ItemDto itemDto) {
        log.info("POST request: добавление предмета {} пользователем с id {}", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID_HEADER) Long userId,
                          @PathVariable ("itemId") long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("PATCH request: обновление предмета {} пользователем с id {}", itemDto, userId);
        return itemClient.update(userId,itemId,itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
                                              @RequestParam(defaultValue = "10", required = false) @Min(1) int size) {
        log.info("GET request: поиск предметов по запросу {}", text);
        return itemClient.searchItems(text,from,size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object>  addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @PathVariable ("itemId") long itemId,
                                 @Validated({Create.class})
                                 @RequestBody CommentDto commentDTO) {
        log.info("POST request: добавление комментария пользователем с id {} к предмету {}", userId, itemId);
        return itemClient.addComment(userId, itemId, commentDTO);
    }
}