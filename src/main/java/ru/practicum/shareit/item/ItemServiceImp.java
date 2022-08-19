package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {

    private final ConversionService conversionService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
        Item item = conversionService.convert(itemDto, Item.class);
        assert item != null;
        item.setOwner(user);
        return conversionService.convert(itemRepository.create(item), ItemDto.class);
    }

    @Override
    public ItemDto update(Long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.getById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет № %d не найден", itemId)));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException(String.format("Пользователь № %d не является владельцем предмета", userId));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return conversionService.convert(itemRepository.update(item), ItemDto.class);
    }

    @Override
    public void delete(long userId, long itemId) {
        itemRepository.delete(userId, itemId);
    }

    @Override
    public Collection<ItemDto> getItems(long userId) {
        return itemRepository.getItems(userId).stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(long itemId) {
        return itemRepository.getById(itemId)
                .map(i -> conversionService.convert(i, ItemDto.class))
                .orElseThrow(() -> new NotFoundException(String.format("Предмет № %d не найден", itemId)));
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text.toLowerCase())
                .stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }
}