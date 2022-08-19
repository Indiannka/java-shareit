package ru.practicum.shareit.requests;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    private Long id;
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
}
