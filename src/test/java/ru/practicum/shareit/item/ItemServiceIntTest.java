package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.Status.WAITING;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntTest {

    private final EntityManager em;
    private final ItemService itemService;

    private final User owner = User.builder().name("owner").email("owner@email.ru").build();
    private final User booker = User.builder().name("booker").email("booker@email.ru").build();
    private final Item item = Item.builder().available(true).description("item").owner(owner).build();
    private final Comment emptyComment = Comment.builder().build();
    private final Item otherItem = Item.builder().available(true).description("otherItem").owner(owner).build();
    private final Booking booking = Booking.builder()
            .start(LocalDateTime.now().minusDays(2))
            .end(LocalDateTime.now().minusDays(1))
            .item(item)
            .booker(booker)
            .status(WAITING)
            .build();
    private final Booking secondBooking = Booking.builder()
            .start(LocalDateTime.now().plusMinutes(5))
            .end(LocalDateTime.now().plusDays(1))
            .item(item)
            .booker(booker)
            .status(APPROVED)
            .build();


    @BeforeEach
    void setUp() {
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);
        em.persist(secondBooking);
    }

    @AfterEach
    void resetSetUp() {
        em.clear();
    }

    @Test
    @DisplayName("IntegrationTest getItemsById method returns item with Bookings by owner id")
    void getItemsByOwnerTest() {
        List<ItemWithBookings> itemWithBookings = (List<ItemWithBookings>) itemService.getItems(owner.getId(), 0, 10);

        assertThat(itemWithBookings).isNotEmpty();
        assertEquals(1, itemWithBookings.size());
        assertThat(booking.getEnd()).isEqualTo(itemWithBookings.get(0).getLastBooking().getEnd());
        assertThat(secondBooking.getStart()).isEqualTo(itemWithBookings.get(0).getNextBooking().getStart());
    }

    @Test
    @DisplayName("IntegrationTest getItemsById method returns item with Bookings by owner id")
    void searchItemsTest() {
        String emptyText = "";
        List<Item> items = (List<Item>) itemService.searchItems(emptyText, 0, 10);
        assertThat(items).isEmpty();
    }

    @Test
    @DisplayName("IntegrationTest update Item method updates description and name to item")
    void updateItemTest() {
        ItemDto itemDto = ItemDto.builder().name("newName").description("newDescr").build();
        itemService.update(owner.getId(), item.getId(), itemDto);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item itemRes = query.setParameter("name", "newName").getSingleResult();


        assertThat(itemService.getById(itemRes.getId()).getName()).isEqualTo(itemRes.getName());
        assertThat(itemService.getById(itemRes.getId()).getClass()).isEqualTo(itemRes.getClass());
        assertThat(itemService.getById(itemRes.getId()).getAvailable()).isEqualTo(itemRes.getAvailable());
        assertThat(itemRes.getDescription()).isEqualTo("newDescr");
        assertThat(itemRes.getRequest()).isNull();

        assertThat(itemService.getById(itemRes.getId())).isNotEqualTo(otherItem);
        assertThat(itemService.getById(itemRes.getId())).isEqualTo(itemRes);
        int hashCode = itemRes.hashCode();
        assertThat(itemRes.getClass().hashCode()).isEqualTo(hashCode);
    }

    @Test
    @DisplayName("IntegrationTest addComment method creates comment to item")
    void addCommentTest() {
        CommentDto commentDto = CommentDto.builder().text("iComment").build();
        itemService.addComment(commentDto, booker.getId(), item.getId());

        TypedQuery<Comment> query = em.createQuery("select c from Comment c where c.item = :item", Comment.class);
        Comment comment = query.setParameter("item", item).getSingleResult();

        assertThat(comment.getAuthor()).isEqualTo(booker);
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getText()).isEqualTo("iComment");
        assertThat(comment.getCreated()).isNotNull();

        assertThat(comment).isNotEqualTo(emptyComment);
        assertThat(comment).isEqualTo(comment);
        int hashCode = comment.hashCode();
        assertThat(comment.getClass().hashCode()).isEqualTo(hashCode);
    }
}