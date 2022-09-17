package ru.practicum.shareit.booking.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;

@Component
@AllArgsConstructor
public class IncomingBookingDtoToBookingConverter  {

    public Booking convert(IncomingBookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(Status.WAITING)
                .build();
    }
}
