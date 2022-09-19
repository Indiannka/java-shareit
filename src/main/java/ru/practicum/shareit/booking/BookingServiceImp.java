package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.converter.BookingConverter;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.Status.REJECTED;
import static ru.practicum.shareit.booking.UserType.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingConverter bookingConverter;

    @Override
    @Transactional(readOnly = true)
    public Booking getById(Long bookingId, Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Бронирование № %d не найдено", bookingId)));
        if (getUserType(booking, userId) == OTHER_USER) {
            throw new NotFoundException(String.format(
                    "Пользователь № %d не является владельцем вещи или заказа", userId));
        }
        return booking;
    }

    @Override
    @Transactional
    public Booking create(Long userId, IncomingBookingDto bookingDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new NotFoundException(String.format("Предмет № %d не найден", bookingDto.getItemId())));
        isAvailable(item);
        checkBookingDates(bookingDto);
        Booking booking = bookingConverter.convert(bookingDto);
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
    @Transactional
    public Booking processRequest(Long userId, Long bookingId, boolean approval) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
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
    @Transactional(readOnly = true)
    public Collection<Booking> getAllByOwner(long userId, String state, String[] sortBy) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        try {
            parseState(state);
        } catch (Exception exception) {
            throw new StateValidationException("Такого параметра не существует " + state);
        }
        Sort sort = setSort(sortBy);
        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findByItemOwnerId(userId, sort);
            case PAST:
                return bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), sort);
            case FUTURE:
                return bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), sort);
            case CURRENT:
                return bookingRepository.findCurrentBookingsByOwnerId(userId, LocalDateTime.now(), sort);
            case WAITING:
                return bookingRepository.findByItemOwnerIdAndStatusEquals(userId, Status.WAITING, sort);
            case REJECTED:
                return bookingRepository.findByItemOwnerIdAndStatusEquals(userId, REJECTED, sort);
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Booking> getAllByBooker(long userId, String state, String[] sortBy) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        Sort sort = setSort(sortBy);
        try {
            parseState(state);
        } catch (Exception exception) {
            throw new StateValidationException("Такого параметра не существует " + state);
        }
            switch (State.valueOf(state)) {
                case ALL:
                    return bookingRepository.findByBookerId(userId, sort);
                case PAST:
                    return bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sort);
                case FUTURE:
                    return bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sort);
                case CURRENT:
                    return bookingRepository.findCurrentByBookerId(userId, LocalDateTime.now(), sort);
                case WAITING:
                    return bookingRepository.findByBookerIdAndStatusEquals(userId, Status.WAITING, sort);
                case REJECTED:
                    return bookingRepository.findByBookerIdAndStatusEquals(userId, REJECTED, sort);
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
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(
                () -> new NotFoundException(String.format("Предмет № %d не найден", booking.getItem().getId())));
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