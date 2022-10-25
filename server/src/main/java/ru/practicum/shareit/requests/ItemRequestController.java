package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.requests.converter.RequestConverter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;
    private final RequestConverter requestConverter;

    @PostMapping
    public ItemRequest create(@RequestHeader(USER_ID_HEADER) long userId,
                              @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                                   @RequestParam String[] sortBy) {
        return itemRequestService.getAllUserRequests(userId, sortBy).stream()
                .map(requestConverter::convert)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(USER_ID_HEADER) long userId,
                                @RequestParam int from,
                                @RequestParam int size,
                                @RequestParam String[] sortBy) {
        return itemRequestService.getAll(userId, from, size, sortBy).stream()
                .map(requestConverter::convert)
                .collect(Collectors.toList());
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getById(@RequestHeader(USER_ID_HEADER) long userId,
                                  @PathVariable long requestId) {
        return requestConverter.convert(itemRequestService.getById(requestId, userId));
    }
}