package ru.practicum.shareit.dto;

import lombok.*;
import ru.practicum.shareit.config.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "Поле description не должно быть пустым", groups = {Create.class})
    private String description;

    private LocalDateTime created;
    private Set<ItemDto> items;
}
