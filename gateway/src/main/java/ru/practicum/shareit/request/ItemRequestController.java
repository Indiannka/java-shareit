package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.dto.ItemRequestDto;

import javax.validation.constraints.Min;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @Validated({Create.class})
                                         @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST request: добавление запроса на вещь {} пользователем с id {}", itemRequestDto, userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                                     @RequestParam(defaultValue = "created;ASC",
                                                           required = false) String[] sortBy) {
        log.info("GET request: запрос списка заявок на предметы, пользователем id {} ", userId);
        return itemRequestClient.getAllUserRequests(userId, sortBy);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
                                         @RequestParam(defaultValue = "10", required = false) @Min(1) int size,
                                         @RequestParam(defaultValue = "created;ASC", required = false) String[] sortBy) {
        log.info("GET request: запрос списка заявок на предметы, пользователем id {}, страница {}," +
                " количество записей {}, в порядке {} ", userId, from, size, sortBy[0]);
        return itemRequestClient.getAll(userId, from, size, sortBy);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable long requestId) {
        log.info("GET request: запрос заявки на предмет {}, пользователем id {} ", requestId, userId);
        return itemRequestClient.getById(userId, requestId);
    }
}