package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.converter.BookingToBookingDtoConverter;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.converter.CommentDtoToCommentConverter;
import ru.practicum.shareit.item.converter.CommentToCommentDtoConverter;
import ru.practicum.shareit.item.converter.ItemDtoToItemConverter;
import ru.practicum.shareit.item.converter.ItemToItemWithBookingsConverter;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {

    private final ItemToItemWithBookingsConverter itemToItemWithBookingsConverter;
    private final ItemDtoToItemConverter itemDtoToItemConverter;
    private final CommentDtoToCommentConverter commentDtoToCommentConverter;
    private final CommentToCommentDtoConverter commentToCommentDtoConverter;
    private final BookingToBookingDtoConverter bookingToBookingDtoConverter;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item create(Long userId, ItemDto itemDto) {
        User user = userService.getById(userId);
        Item item = itemDtoToItemConverter.convert(itemDto);
        if (item == null) {
            throw new NotFoundException(String.format(
                    "Отсутствуют параметры входящего объекта itemDto %s ", itemDto));
        }
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
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
    public Collection<ItemWithBookings> getItems(Long userId) {
        Collection<ItemWithBookings> items = itemRepository.findAllByOwnerId(userId).stream()
                .map(itemToItemWithBookingsConverter::convert)
                .collect(Collectors.toList());
        for (ItemWithBookings item : items) {
            setBookingsToItem(item);
            setCommentsToItem(item);
        }
        return items;
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет № %d не найден", itemId)));
    }

    @Override
    public ItemWithBookings getById(Long itemId, Long userId) {
        Item item = getById(itemId);
        ItemWithBookings itemWithBookingDates = itemToItemWithBookingsConverter.convert(item);
        if (itemWithBookingDates == null) {
            throw new NotFoundException(String.format(
                    "Отсутствуют параметры конвертируемого объекта item %s ", item));
        }
        if (userId.equals(item.getOwner().getId())) {
            setBookingsToItem(itemWithBookingDates);
        }
        setCommentsToItem(itemWithBookingDates);
        return itemWithBookingDates;
    }

    @Override
    public Collection<Item> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return List.copyOf(itemRepository.searchItems("%" + text.toLowerCase() + "%"));
    }

    @Override
    public Comment addComment(CommentDto commentDTO, Long userId, Long itemId) {
        User user = userService.getById(userId);
        Booking usersBooking = bookingRepository
                .findFirstByBookerIdAndItemIdAndStartIsBefore(userId, itemId, LocalDateTime.now()).orElseThrow(
                        () -> new ValidationException(String.format("Предмет № %d не найден у пользователя № %d", itemId, userId)));
        Comment comment = commentDtoToCommentConverter.convert(commentDTO);
        if (comment == null) {
            throw new NotFoundException(String.format(
                    "Отсутствуют параметры входящего объекта commentDTO %s ", commentDTO));
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
        lastBooking.ifPresent(booking -> item.setLastBooking(bookingToBookingDtoConverter.convert(booking)));
        nextBooking.ifPresent(booking -> item.setNextBooking(bookingToBookingDtoConverter.convert(booking)));
    }

    private void setCommentsToItem(ItemWithBookings item) {
        List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                .map(commentToCommentDtoConverter::convert)
                .collect(Collectors.toList());
        if (comments.isEmpty()) {
            item.setComments(Collections.emptyList());
        } else {
            item.setComments(comments);
        }
    }
}