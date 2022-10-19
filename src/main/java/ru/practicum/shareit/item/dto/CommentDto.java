package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.config.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentDto {
    private Long id;

    @NotBlank(message = "Поле text не должно быть пустым", groups = {Create.class})
    private String text;

    private String authorName;

    private LocalDateTime created;
}
