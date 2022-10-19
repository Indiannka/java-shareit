package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.config.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {
    @NotNull(groups = {Update.class})
    private Long id;

    @NotBlank(message = "Поле name не должно быть пустым", groups = {Create.class})
    private String name;

    @Email(groups = {Create.class})
    @NotBlank(message = "Поле email не должно быть пустым", groups = {Create.class})
    private String email;
}