package ru.practicum.shareit.booking.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;

public class BookingToIncomingBookingDto implements Converter<Booking, IncomingBookingDto> {

    @Override
    public IncomingBookingDto convert(Booking booking) {
        return IncomingBookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }
}
