package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findFirstByBookerIdAndItemIdAndStartIsBefore(Long bookerId, Long itemId, LocalDateTime dateTime);

    Optional<Booking> findFirstByItemIdAndStartIsAfterOrderByStartAsc(long itemId, LocalDateTime dateTime);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeOrderByEndDesc(long itemId, LocalDateTime dateTime);

    Collection<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select booking from Booking booking" +
            " where booking.booker.id = ?1" +
            " and booking.start <= ?2" +
            " and booking.end >= ?2" +
            " order by booking.start desc")
    Collection<Booking> findCurrentByBookerId(Long bookerId, LocalDateTime now);

    Collection<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    Collection<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    Collection<Booking> findByBookerIdAndStatusEqualsOrderByStartDesc(Long bookerId, Status status);

    Collection<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select booking from Booking booking" +
            " where booking.item.owner.id = ?1" +
            " and booking.start <= ?2" +
            " and booking.end >= ?2" +
            " order by booking.start desc")
    Collection<Booking> findCurrentBookingsByOwnerId(Long ownerId, LocalDateTime now);

    Collection<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    Collection<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    Collection<Booking> findByItemOwnerIdAndStatusEqualsOrderByStartDesc(Long bookerId, Status status);
}