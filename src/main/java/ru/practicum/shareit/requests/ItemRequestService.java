package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequest create(Long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequest> getAllUserRequests(Long userId, String[] sortBy);

    Collection<ItemRequest> getAll(Long userId, int from, int size, String[] sortBy);

    ItemRequest getById(long requestId, long userId);
}

