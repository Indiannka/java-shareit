package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.dto.BookingDto;
import ru.practicum.shareit.dto.CommentDto;

import java.util.Collection;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemWithBookings {
    private Long id;
    private String name;
    private Boolean available;
    private String description;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Collection<CommentDto> comments;
}
