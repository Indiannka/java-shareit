package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class Item {
    private Long id;
    private String name;
    private User owner;
    private Boolean available;
    private String description;
    private ItemRequest request;
}