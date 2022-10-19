package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.practicum.shareit.booking.Status.REJECTED;
import static ru.practicum.shareit.booking.Status.WAITING;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntTest {

    private final EntityManager em;
    private final BookingService bookingService;

    private final User owner = User.builder().name("owner").email("owner@email.ru").build();
    private final User booker = User.builder().name("booker").email("booker@email.ru").build();
    private final Item item = Item.builder().available(true).description("item").owner(owner).build();

    private final Booking booking = Booking.builder()
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(3))
            .item(item)
            .booker(booker)
            .status(WAITING)
            .build();

    private final int page = 0;
    private final int size = 10;
    private final String[] sortBy = new String[] {"start;DESC"};

    @BeforeEach
    void setUp() {
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);
    }

    @AfterEach
    void resetSetUp() {
        em.clear();
    }

    @Test
    void createBooking() {
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(4))
                .itemId(item.getId())
                .build();
        User otherBooker = User.builder().name("otherBooker").email("otherBooker@email.ru").build();
        em.persist(otherBooker);
        bookingService.create(otherBooker.getId(), incomingBookingDto);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.booker = :booker", Booking.class);
        Booking bookingRes = query.setParameter("booker", otherBooker).getSingleResult();

        assertThat(bookingRes).isNotNull();
        assertThat(bookingRes.getStatus()).isEqualTo(WAITING);

        assertThat(bookingRes).isNotEqualTo(booking);

        int hashCode = booking.hashCode();
        Assertions.assertThat(booking.getClass().hashCode()).isEqualTo(hashCode);
    }

    @Test
    void getAllByBookerWithFutureStateTest() {
        List<Booking> bookings = (List<Booking>) bookingService.getAllByBooker(booker.getId(), "FUTURE",page, size, sortBy);
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0)).isEqualTo(booking);
        assertThat(bookings.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(bookings.get(0).getEnd()).isEqualTo(booking.getEnd());
    }

    @Test
    @DisplayName("IntegrationTest processRequest method changes status to rejected")
    void processRequestTest() {
        assertThat(booking).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(WAITING);

        bookingService.processRequest(owner.getId(), booking.getId(),false);
        assertThat(booking.getStatus()).isEqualTo(REJECTED);

    }
}