package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.requests.converter.RequestConverter;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final RequestConverter requestConverter;

    @Override
    @Transactional
    public ItemRequest create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        ItemRequest itemRequest = requestConverter.convert(itemRequestDto);
        if (itemRequest == null) {
            throw new NotFoundException(
                    "Отсутствуют параметры входящего объекта itemRequestDto " + itemRequestDto);
        }
        itemRequest.setRequestor(user);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequest> getAllUserRequests(Long userId, String[] sortBy) {
        Sort sort = setSort(sortBy);
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        return itemRequestRepository.getAllByRequestorId(userId, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequest> getAll(Long userId, int from, int size, String[] sortBy) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        Sort sort = setSort(sortBy);
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        return itemRequestRepository.findAll(pageable).stream()
                .filter(itemRequest -> !Objects.equals(itemRequest.getRequestor().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequest getById(long requestId, long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        return itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос № %d не найден", requestId)));
    }

    private Sort setSort(String[] sortBy) {
        return Sort.by(
                Arrays.stream(sortBy)
                        .map(s -> s.split(";", 2))
                        .map(array ->
                                new Sort.Order(array[1].equalsIgnoreCase("DESC") ?
                                        Sort.Direction.DESC : Sort.Direction.ASC,array[0]).ignoreCase()
                        ).collect(Collectors.toList()));
    }
}