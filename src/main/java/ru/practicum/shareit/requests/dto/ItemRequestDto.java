package ru.practicum.shareit.requests.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "Поле description не должно быть пустым", groups = {Create.class})
    private String description;

    private final LocalDateTime created;
    private Set<ItemDto> items;
}
