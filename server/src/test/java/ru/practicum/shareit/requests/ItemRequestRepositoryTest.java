package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private final Sort sort = Sort.by(Sort.Direction.ASC,"created");

    private final User requestor = User.builder().name("requestor").email("requestor@email.ru").build();
    private final ItemRequest itemRequest = ItemRequest.builder().created(LocalDateTime.now()).description("request").requestor(requestor).build();

    @Test
    void getAllByRequestorIdTest() {
        em.persist(itemRequest);
        em.persist(requestor);

        List<ItemRequest> requests = (List<ItemRequest>) itemRequestRepository.getAllByRequestorId(requestor.getId(), sort);
        assertEquals(1, requests.size());
    }
}