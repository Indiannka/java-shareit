package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestIntTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    private final String[] sortBy = new String[] {"created;ASC"};

    private final User owner = User.builder().name("owner").email("owner@email.ru").build();
    private final Item item = Item.builder().available(true).description("item").owner(owner).build();
    private final ItemRequest emptyRequest = ItemRequest.builder().build();
    private final User requestor = User.builder().name("requestor").email("requestor@email.ru").build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("needItem").build();

    @BeforeEach
    void setUp() {
        em.persist(requestor);
        em.persist(owner);
        em.persist(item);
    }

    @AfterEach
    void resetSetUp() {
        em.clear();
    }

    @Test
    void createItemRequest() {
        itemRequestService.create(requestor.getId(),itemRequestDto);
        TypedQuery<ItemRequest> query = em.createQuery("select i from ItemRequest i where i.description = :desc", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("desc", "needItem").getSingleResult();

        assertThat(itemRequest).isNotNull();
        assertThat(itemRequest.getRequestor()).isEqualTo(requestor);
        assertThat(itemRequest.getCreated()).isNotNull();
        assertThat(itemRequest.getDescription()).isEqualTo("needItem");
        assertThat(itemRequest).isNotEqualTo(emptyRequest);
        int hashCode = itemRequest.hashCode();
        assertThat(itemRequest.getClass().hashCode()).isEqualTo(hashCode);
    }

    @Test
    void getAllRequestsByUser() {
        ItemRequest itemRequest = ItemRequest.builder()
                .created(LocalDateTime.now()).requestor(requestor).description("smth").build();
        em.persist(itemRequest);

        List<ItemRequest> itemRequestList = (List<ItemRequest>) itemRequestService.getAll(owner.getId(), 0, 10, sortBy);
        assertThat(itemRequestList.size()).isEqualTo(1);
        assertThat(itemRequestList.get(0)).isEqualTo(itemRequest);

        List<ItemRequest> emptyRequestList = (List<ItemRequest>) itemRequestService.getAll(requestor.getId(), 0, 10, sortBy);
        assertThat(emptyRequestList.size()).isEqualTo(0);
    }
}