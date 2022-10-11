package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private final User owner = User.builder().name("owner").email("owner@email.ru").build();
    private final Item item = Item.builder().available(true).name("text").description("itemText").owner(owner).build();
    private final ItemRequest itemRequest = ItemRequest.builder().created(LocalDateTime.now()).description("request").build();
    private final Item otherItem = Item.builder().available(true).name("smth").description("smthDescription").owner(owner).request(itemRequest).build();

    private final Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        itemRequestRepository.save(itemRequest);
        userRepository.save(owner);
        itemRepository.save(item);
        itemRepository.save(otherItem);
    }

    @AfterEach
    void resetSetUp() {
        em.clear();
    }

    @Test
    void searchItemsTest() {
        List<Item> items = itemRepository.searchItems("%text%", pageable).toList();
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }

    @Test
    void findAllByOwnerIdTest() {
        List<Item> items = itemRepository.findAllByOwnerId(owner.getId(), pageable).toList();
        assertEquals(2, items.size());
    }

    @Test
    void findAllByRequestIdTest() {
        List<Item> items = (List<Item>) itemRepository.findAllByRequestId(itemRequest.getId());
        assertEquals(1, items.size());
        assertEquals(otherItem, items.get(0));
    }
}