package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.requests.converter.RequestConverter;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;
    private final RequestConverter requestConverter;

    @PostMapping
    public ItemRequest create(@RequestHeader(USER_ID_HEADER) long userId,
                                     @Validated({Create.class})
                                     @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST request: добавление запроса на вещь {} пользователем с id {}", itemRequestDto, userId);
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                                   @RequestParam(value = "sort", defaultValue = "created;ASC",
                                                           required = false) String[] sortBy) {
        log.info("GET request: запрос списка заявок на предметы, пользователем id {} ", userId);
        return itemRequestService.getAllUserRequests(userId, sortBy).stream()
                .map(requestConverter::convert)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(USER_ID_HEADER) long userId,
                                @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
                                @RequestParam(defaultValue = "10", required = false) @Min(1) int size,
                                @RequestParam(value = "sort", defaultValue = "created;ASC", required = false) String[] sortBy) {
        log.info("GET request: запрос списка заявок на предметы, пользователем id {}, страница {}," +
                " количество записей {}, в порядке {} ", userId, from, size, sortBy[0]);
        return itemRequestService.getAll(userId, from, size, sortBy).stream()
                .map(requestConverter::convert)
                .collect(Collectors.toList());
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getById(@RequestHeader(USER_ID_HEADER) long userId,
                              @PathVariable long requestId) {
        log.info("GET request: запрос заявки на предмет {}, пользователем id {} ", requestId, userId);
        return requestConverter.convert(itemRequestService.getById(requestId, userId));
    }
}