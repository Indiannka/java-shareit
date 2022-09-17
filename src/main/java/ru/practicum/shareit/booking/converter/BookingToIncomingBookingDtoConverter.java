package ru.practicum.shareit.booking.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;

@Component
@AllArgsConstructor
public class BookingToIncomingBookingDtoConverter {

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
