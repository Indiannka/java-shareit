package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final ConversionService conversionService;
    private static final int OWNER = 1;
    private static final int BOOKER = 2;
    private static final int OTHER_USER = 0;

    @Override
    public Booking getById(Long bookingId, Long userId) {
        userService.getById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Бронирование № %d не найдено", bookingId)));
        if (getUserType(booking, userId) == OTHER_USER) {
            throw new NotFoundException(String.format(
                    "Пользователь № %d не является владельцем вещи или заказа", userId));
        }
        return booking;
    }

    @Override
    public Booking create(Long userId, IncomingBookingDto bookingDto) {
        User user = userService.getById(userId);
        Item item = itemService.getById(bookingDto.getItemId());
        isAvailable(item);
        checkBookingDates(bookingDto);
        Booking booking = conversionService.convert(bookingDto, Booking.class);
        assert booking != null;
        booking.setBooker(user);
        booking.setItem(item);
        if (getUserType(booking, userId) == OWNER) {
            throw new NotFoundException("Пользователь не может бронировать свои предметы");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking processRequest(Long userId, Long bookingId, Boolean approval) {
        userService.getById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Бронирование № %d не найдено", bookingId)));
        if (getUserType(booking, userId) != OWNER) {
            throw new NotFoundException(String.format(
                         "Пользователь № %d не является владельцем вещи", userId));
        }
        checkBookingStatus(booking);
        if (Boolean.TRUE.equals(approval)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Collection<Booking> getAllByOwner(long userId, String state) {
        userService.getById(userId);
        if (Arrays.stream(State.values()).map(Enum::name).noneMatch(s -> s.equals(state))) {
            throw new StateValidationException("Такого параметра не существует " + state);
        }
        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findByItem_Owner_IdOrderByStartDesc(userId);
            case PAST:
                return bookingRepository.findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case CURRENT:
                return bookingRepository.findCurrentBookingsByOwner_Id(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByItem_Owner_IdAndStatusEqualsOrderByStartDesc(userId, Status.WAITING);
            case REJECTED:
                return bookingRepository.findByItem_Owner_IdAndStatusEqualsOrderByStartDesc(userId, Status.REJECTED);
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<Booking> getAllByBooker(long userId, String state) {
        userService.getById(userId);
        if (Arrays.stream(State.values()).map(Enum::name).noneMatch(s -> s.equals(state))) {
            throw new StateValidationException("Такого параметра не существует " + state);
        }
        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findByBooker_IdOrderByStartDesc(userId);
            case PAST:
                return bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case CURRENT:
                return bookingRepository.findCurrentByBooker_Id(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByBooker_IdAndStatusEqualsOrderByStartDesc(userId, Status.WAITING);
            case REJECTED:
                return bookingRepository.findByBooker_IdAndStatusEqualsOrderByStartDesc(userId, Status.REJECTED);
        }
        return Collections.emptyList();
    }

    private void isAvailable(Item item) {
        Boolean isAvailable = item.getAvailable();
        if (Boolean.FALSE.equals(isAvailable)) {
            throw new NotAvailableException("Предмет недоступен для бронирования в данный момент");
        }
    }

    private void checkBookingStatus(Booking booking) {
        Status status = booking.getStatus();
        if (!status.equals(Status.WAITING)) {
            throw new StatusProcessException("Статус заказа уже переведен в " + booking.getStatus());
        }
    }

    private int getUserType(Booking booking, Long userId) {
        Item item = itemService.getById(booking.getItem().getId());
        if (userId.equals(item.getOwner().getId())) {
            return OWNER;
        }
        if (userId.equals(booking.getBooker().getId())) {
            return BOOKER;
        }
        return OTHER_USER;
    }

    private void checkBookingDates(IncomingBookingDto incomingBookingDto) {
        Boolean isStartDateBeforeEnd = incomingBookingDto.getStart().isBefore(incomingBookingDto.getEnd());
        if (Boolean.FALSE.equals(isStartDateBeforeEnd)) {
            throw new ValidationException("Дата окончания бронирования должна быть позже даты начала бронирования.");
        }
    }
}