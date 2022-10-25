package ru.practicum.shareit.booking;


import ru.practicum.shareit.dto.IncomingBookingDto;

import java.util.Collection;

public interface BookingService {

    Booking getById(Long bookingId, Long userId);

    Booking create(long userId, IncomingBookingDto bookingDto);

    Booking processRequest(Long userId, Long bookingId, boolean approval);

    Collection<Booking> getAllByOwner(long userId, String state, int from, int size, String[] sortBy);

    Collection<Booking> getAllByBooker(long userId, String state, int from, int size, String[] sortBy);
}

