package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    @NotNull(groups = {Update.class})
    private Long id;

    @NotBlank(message = "Поле name не должно быть пустым", groups = {Create.class})
    private String name;

    @NotBlank(message = "Поле description не должно быть пустым", groups = {Create.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;

    private Long requestId;
}