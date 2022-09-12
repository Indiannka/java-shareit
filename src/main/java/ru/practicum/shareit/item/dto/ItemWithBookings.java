package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;


@Data
@Builder
public class ItemWithBookings {
    private Long id;
    private String name;
    private Boolean available;
    private String description;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Collection<CommentDTO> comments;
}
