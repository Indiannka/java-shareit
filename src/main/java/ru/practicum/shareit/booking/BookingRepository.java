package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findFirstByBooker_IdAndItem_IdAndStartIsBefore(Long bookerId, Long itemId, LocalDateTime dateTime);

    Optional<Booking> findFirstByItem_IdAndStartIsAfterOrderByStartAsc(long itemId, LocalDateTime dateTime);

    Optional<Booking> findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(long itemId, LocalDateTime dateTime);

    Collection<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    @Query("select booking from Booking booking" +
            " where booking.booker.id = ?1" +
            " and booking.start <= ?2" +
            " and booking.end >= ?2" +
            " order by booking.start desc")
    Collection<Booking> findCurrentByBooker_Id(Long bookerId, LocalDateTime now);

    Collection<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    Collection<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    Collection<Booking> findByBooker_IdAndStatusEqualsOrderByStartDesc(Long bookerId, Status status);

    Collection<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

    @Query("select booking from Booking booking" +
            " where booking.item.owner.id = ?1" +
            " and booking.start <= ?2" +
            " and booking.end >= ?2" +
            " order by booking.start desc")
    Collection<Booking> findCurrentBookingsByOwner_Id(Long ownerId, LocalDateTime now);

    Collection<Booking> findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    Collection<Booking> findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    Collection<Booking> findByItem_Owner_IdAndStatusEqualsOrderByStartDesc(Long bookerId, Status status);
}