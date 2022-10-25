package ru.practicum.shareit.booking.converter;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.Status;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.dto.BookingDto;
import ru.practicum.shareit.dto.IncomingBookingDto;


@Component
public class BookingConverter {
    public BookingDto convert(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public IncomingBookingDto convertToIncomingDto(Booking booking) {
        return IncomingBookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public Booking convert(IncomingBookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(Status.WAITING)
                .build();
    }
}