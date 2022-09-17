package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.converter.IncomingBookingDtoToBookingConverter;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.Status.REJECTED;
import static ru.practicum.shareit.booking.UserType.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final IncomingBookingDtoToBookingConverter incomingBookingDtoToBookingConverter;

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
        Booking booking = incomingBookingDtoToBookingConverter.convert(bookingDto);
        if (booking == null) {
            throw new NotFoundException(String.format(
                    "Отсутствуют параметры входящего заказа bookingDto %s ", bookingDto));
        }
        booking.setBooker(user);
        booking.setItem(item);
        if (getUserType(booking, userId) == OWNER) {
            throw new NotFoundException("Пользователь не может бронировать свои предметы");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking processRequest(Long userId, Long bookingId, boolean approval) {
        userService.getById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Бронирование № %d не найдено", bookingId)));
        if (getUserType(booking, userId) != OWNER) {
            throw new NotFoundException(String.format(
                         "Пользователь № %d не является владельцем вещи", userId));
        }
        checkBookingStatus(booking);
        booking.setStatus(approval ? APPROVED : REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Collection<Booking> getAllByOwner(long userId, String state) {
        userService.getById(userId);
        try {
            parseState(state);
        } catch (Exception exception) {
            throw new StateValidationException("Такого параметра не существует " + state);
        }
        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
            case PAST:
                return bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case CURRENT:
                return bookingRepository.findCurrentBookingsByOwnerId(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(userId, Status.WAITING);
            case REJECTED:
                return bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(userId, REJECTED);
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<Booking> getAllByBooker(long userId, String state) {
        userService.getById(userId);
        try {
            parseState(state);
        } catch (Exception exception) {
            throw new StateValidationException("Такого параметра не существует " + state);
        }
            switch (State.valueOf(state)) {
                case ALL:
                    return bookingRepository.findByBookerIdOrderByStartDesc(userId);
                case PAST:
                    return bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                case FUTURE:
                    return bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                case CURRENT:
                    return bookingRepository.findCurrentByBookerId(userId, LocalDateTime.now());
                case WAITING:
                    return bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(userId, Status.WAITING);
                case REJECTED:
                    return bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(userId, REJECTED);
            }
        return Collections.emptyList();
    }

    private void isAvailable(Item item) {
        boolean isAvailable = item.getAvailable();
        if (!isAvailable) {
            throw new NotAvailableException("Предмет недоступен для бронирования в данный момент");
        }
    }

    private void checkBookingStatus(Booking booking) {
        Status status = booking.getStatus();
        if (!status.equals(Status.WAITING)) {
            throw new StatusProcessException("Статус заказа уже переведен в " + booking.getStatus());
        }
    }

    private UserType getUserType(Booking booking, Long userId) {
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
        boolean isStartDateBeforeEnd = incomingBookingDto.getStart().isBefore(incomingBookingDto.getEnd());
        if (!isStartDateBeforeEnd) {
            throw new ValidationException("Дата окончания бронирования должна быть позже даты начала бронирования.");
        }
    }

    private State parseState(String value) {
        return State.valueOf(value);
    }
}