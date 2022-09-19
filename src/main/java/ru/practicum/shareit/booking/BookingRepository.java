package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findFirstByBookerIdAndItemIdAndStartIsBefore(Long bookerId, Long itemId, LocalDateTime dateTime);

    Optional<Booking> findFirstByItemIdAndStartIsAfterOrderByStartAsc(long itemId, LocalDateTime dateTime);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeOrderByEndDesc(long itemId, LocalDateTime dateTime);

    Collection<Booking> findByBookerId(Long bookerId, Sort sort);

    @Query("select booking from Booking booking" +
            " where booking.booker.id = ?1" +
            " and booking.start <= ?2" +
            " and booking.end >= ?2")
    Collection<Booking> findCurrentByBookerId(Long bookerId, LocalDateTime now, Sort sort);

    Collection<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Sort sort);

    Collection<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime now, Sort sort);

    Collection<Booking> findByBookerIdAndStatusEquals(Long bookerId, Status status, Sort sort);

    Collection<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    @Query("select booking from Booking booking" +
            " where booking.item.owner.id = ?1" +
            " and booking.start <= ?2" +
            " and booking.end >= ?2")
    Collection<Booking> findCurrentBookingsByOwnerId(Long ownerId, LocalDateTime now, Sort sort);

    Collection<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime now, Sort sort);

    Collection<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime now, Sort sort);

    Collection<Booking> findByItemOwnerIdAndStatusEquals(Long bookerId, Status status, Sort sort);
}