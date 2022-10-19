package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.converter.BookingConverter;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.converter.CommentConverter;
import ru.practicum.shareit.item.converter.ItemConverter;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {

    private final ItemConverter itemConverter;
    private final CommentConverter commentConverter;
    private final BookingConverter bookingConverter;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public Item create(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        Item item = itemConverter.convert(itemDto);
        if (item == null) {
            throw new NotFoundException("Отсутствуют параметры входящего объекта itemDto");
        }
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(String.format("Запрос на предмеь № %d не найден", itemDto.getRequestId())));
            item.setRequest(itemRequest);
        }
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = getById(itemId);
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException(String.format("Пользователь № %d не является владельцем предмета", userId));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemRepository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemWithBookings> getItems(Long userId, int from, int size, String[] sortBy) {
        Sort sort = setSort(sortBy);
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        Collection<ItemWithBookings> items = itemRepository.findAllByOwnerId(userId, pageable).toList().stream()
                .map(itemConverter::convertToItemWithBookings)
                .collect(Collectors.toList());
        for (ItemWithBookings item : items) {
            setBookingsToItem(item);
            setCommentsToItem(item);
        }
        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public Item getById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет № %d не найден", itemId)));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemWithBookings getById(Long itemId, Long userId) {
        Item item = getById(itemId);
        ItemWithBookings itemWithBookingDates = itemConverter.convertToItemWithBookings(item);
        if (itemWithBookingDates == null) {
            throw new NotFoundException(
                    "Отсутствуют параметры конвертируемого объекта item " + item.getId());
        }
        if (userId.equals(item.getOwner().getId())) {
            setBookingsToItem(itemWithBookingDates);
        }
        setCommentsToItem(itemWithBookingDates);
        return itemWithBookingDates;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Item> searchItems(String text, int from, int size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return itemRepository.searchItems("%" + text.toLowerCase() + "%", pageable).toList();
    }

    @Override
    @Transactional
    public Comment addComment(CommentDto commentDTO, Long userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        Booking usersBooking = bookingRepository
                .findFirstByBookerIdAndItemIdAndStartIsBefore(userId, itemId, LocalDateTime.now()).orElseThrow(
                        () -> new ValidationException(String.format("Предмет № %d не найден у пользователя № %d", itemId, userId)));
        Comment comment = commentConverter.convert(commentDTO);
        if (comment == null) {
            throw new NotFoundException(
                    "Отсутствуют параметры входящего объекта commentDTO " + commentDTO);
        }
        comment.setItem(usersBooking.getItem());
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    private void setBookingsToItem(ItemWithBookings item) {
        Optional<Booking> lastBooking = bookingRepository
                .findFirstByItemIdAndEndIsBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
        Optional<Booking> nextBooking = bookingRepository
                .findFirstByItemIdAndStartIsAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
        lastBooking.ifPresent(booking -> item.setLastBooking(bookingConverter.convert(booking)));
        nextBooking.ifPresent(booking -> item.setNextBooking(bookingConverter.convert(booking)));
    }

    private void setCommentsToItem(ItemWithBookings item) {
        List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                .map(commentConverter::convert)
                .collect(Collectors.toList());
        if (comments.isEmpty()) {
            item.setComments(Collections.emptyList());
        } else {
            item.setComments(comments);
        }
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