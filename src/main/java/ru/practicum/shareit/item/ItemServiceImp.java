package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDTO;
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

    private final ConversionService conversionService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item create(Long userId, ItemDto itemDto) {
        User user = userService.getById(userId);
        Item item = conversionService.convert(itemDto, Item.class);
        assert item != null;
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
                .map(item -> conversionService.convert(item, ItemWithBookings.class))
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
        ItemWithBookings itemWithBookingDates = conversionService.convert(item, ItemWithBookings.class);
        assert itemWithBookingDates != null;
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
    public Comment addComment(CommentDTO commentDTO, Long userId, Long itemId) {
        User user = userService.getById(userId);
        Booking usersBooking = bookingRepository
                .findFirstByBooker_IdAndItem_IdAndStartIsBefore(userId, itemId, LocalDateTime.now()).orElseThrow(
                        () -> new ValidationException(String.format("Предмет № %d не найден у пользователя № %d", itemId, userId)));
        Comment comment = conversionService.convert(commentDTO, Comment.class);
        assert comment != null;
        comment.setItem(usersBooking.getItem());
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    private void setBookingsToItem(ItemWithBookings item) {
        Optional<Booking> lastBooking = bookingRepository
                .findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
        Optional<Booking> nextBooking = bookingRepository
                .findFirstByItem_IdAndStartIsAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
        lastBooking.ifPresent(booking -> item.setLastBooking(conversionService.convert(booking, BookingDto.class)));
        nextBooking.ifPresent(booking -> item.setNextBooking(conversionService.convert(booking, BookingDto.class)));
    }

    private void setCommentsToItem(ItemWithBookings item) {
        List<CommentDTO> comments = commentRepository.findAllByItem_Id(item.getId()).stream()
                .map(comment -> conversionService.convert(comment, CommentDTO.class))
                .collect(Collectors.toList());
        if (comments.isEmpty()) {
            item.setComments(Collections.emptyList());
        } else {
            item.setComments(comments);
        }
    }
}