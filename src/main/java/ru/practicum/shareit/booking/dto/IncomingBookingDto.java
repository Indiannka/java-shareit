package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class IncomingBookingDto {

    @NotNull(groups = {Update.class})
    private Long id;

    @NotNull(groups = {Create.class})
    private Long itemId;


    @NotNull(groups = {Create.class})
    @Future
    private LocalDateTime start;

    @Future
    @NotNull(groups = {Create.class})
    private LocalDateTime end;

    private Status status;
}
