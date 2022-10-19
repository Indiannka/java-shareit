package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.Status.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    private final User owner = User.builder().name("owner").email("owner@email.ru").build();
    private final User booker = User.builder().name("booker").email("booker@email.ru").build();
    private final Item item = Item.builder().available(true).description("item").owner(owner).build();
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
    private final Booking currentBooking = Booking.builder()
            .start(LocalDateTime.now().minusMinutes(5))
            .end(LocalDateTime.now().plusDays(1))
            .item(item)
            .booker(booker)
            .status(REJECTED)
            .build();

    private final Sort sort = Sort.by(Sort.Direction.DESC,"start");
    private final Pageable pageable = PageRequest.of(0, 10, sort);

    @BeforeEach
    void setUp() {
        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(booking);
        em.persist(secondBooking);
        em.persist(currentBooking);
    }

    @AfterEach
    void resetSetUp() {
        em.clear();
    }

    @Test
    void findFirstByBookerIdAndItemIdAndStartIsBeforeTest() {
        Booking bookingResult = bookingRepository
                .findFirstByBookerIdAndItemIdAndStartIsBefore(booker.getId(),item.getId(), LocalDateTime.now()).get();
        assertEquals(booking, bookingResult);
    }

    @Test
    void findFirstByItemIdAndStartIsAfterOrderByStartAscTest() {
        Booking bookingResult = bookingRepository
                .findFirstByItemIdAndStartIsAfterOrderByStartAsc(item.getId(), LocalDateTime.now()).get();
        assertEquals(secondBooking, bookingResult);
    }

    @Test
    void findFirstByItemIdAndEndIsBeforeOrderByEndDescTest() {
        Booking bookingResult = bookingRepository
                .findFirstByItemIdAndEndIsBeforeOrderByEndDesc(item.getId(), LocalDateTime.now()).get();
        assertEquals(booking, bookingResult);
    }

    @Test
    void findByBookerIdTest() {
        List<Booking> bookings = bookingRepository.findByBookerId(booker.getId(), pageable).toList();
        assertEquals(3, bookings.size());
        assertEquals(secondBooking, bookings.get(0));
    }

    @Test
    void findCurrentByBookerIdTest() {
        List<Booking> bookings = bookingRepository.findCurrentByBookerId(booker.getId(),LocalDateTime.now(), pageable).toList();
        assertEquals(1, bookings.size());
        assertEquals(currentBooking, bookings.get(0));
    }

    @Test
    void findByBookerIdAndStartIsAfterTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartIsAfter(booker.getId(),LocalDateTime.now(), pageable).toList();
        assertEquals(1, bookings.size());
        assertEquals(secondBooking, bookings.get(0));
    }

    @Test
    void findByBookerIdAndEndIsBeforeTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(booker.getId(),LocalDateTime.now(), pageable).toList();
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void findByBookerIdAndStatusEqualsApproved() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatusEquals(booker.getId(), APPROVED, pageable).toList();
        assertEquals(1, bookings.size());
        assertEquals(secondBooking, bookings.get(0));
    }

    @Test
    void findByItemOwnerIdTest() {
        List<Booking> bookings = bookingRepository.findByItemOwnerId(owner.getId(), pageable).toList();
        assertEquals(3, bookings.size());
        assertEquals(secondBooking, bookings.get(0));
    }

    @Test
    void findCurrentBookingsByOwnerIdTest() {
        List<Booking> bookings = bookingRepository.findCurrentBookingsByOwnerId(owner.getId(), LocalDateTime.now(), pageable).toList();
        assertEquals(1, bookings.size());
        assertEquals(currentBooking, bookings.get(0));
    }

    @Test
    void findByItemOwnerIdAndStartIsAfterTest() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(owner.getId(), LocalDateTime.now(), pageable).toList();
        assertEquals(1, bookings.size());
        assertEquals(secondBooking, bookings.get(0));
    }

    @Test
    void findByItemOwnerIdAndEndIsBeforeTest() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(owner.getId(), LocalDateTime.now(), pageable).toList();
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void findByItemOwnerIdAndStatusEqualsRejected() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatusEquals(booker.getId(), REJECTED, pageable).toList();
        assertEquals(1, bookings.size());
        assertEquals(currentBooking, bookings.get(0));
    }
}