package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.config.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDTO {
    private Long id;

    @NotNull(groups = {Create.class})
    @NotBlank(message = "Поле text не должно быть пустым", groups = {Create.class})
    private String text;

    private String authorName;

    private LocalDateTime created;
}
