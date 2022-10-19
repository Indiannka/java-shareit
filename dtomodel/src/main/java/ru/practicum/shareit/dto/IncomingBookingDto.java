package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.practicum.shareit.Status;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class IncomingBookingDto {

    @NotNull(groups = {Update.class})
    private Long id;

    @NotNull(groups = {Create.class})
    private Long itemId;

    @NotNull(groups = {Create.class})
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;

    @FutureOrPresent(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime end;

    private Status status;
}
