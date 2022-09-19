package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.IncomingBookingDto;

import java.util.Collection;

public interface BookingService {

    Booking getById(Long bookingId, Long userId);

    Booking create(Long userId, IncomingBookingDto bookingDto);

    Booking processRequest(Long userId, Long bookingId, boolean approval);

    Collection<Booking> getAllByOwner(long userId, String state, String[] sortBy);

    Collection<Booking> getAllByBooker(long userId, String state, String[] sortBy);
}

