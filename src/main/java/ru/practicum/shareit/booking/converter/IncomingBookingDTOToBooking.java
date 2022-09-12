package ru.practicum.shareit.booking.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;

public class IncomingBookingDTOToBooking implements Converter<IncomingBookingDto, Booking> {

    @Override
    public Booking convert(IncomingBookingDto bookingDTO) {
        return Booking.builder()
                .id(bookingDTO.getId())
                .start(bookingDTO.getStart())
                .end(bookingDTO.getEnd())
                .status(Status.WAITING)
                .build();
    }
}
