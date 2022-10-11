package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findFirstByBookerIdAndItemIdAndStartIsBefore(Long bookerId, Long itemId, LocalDateTime dateTime);

    Optional<Booking> findFirstByItemIdAndStartIsAfterOrderByStartAsc(long itemId, LocalDateTime dateTime);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeOrderByEndDesc(long itemId, LocalDateTime dateTime);

    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    @Query("select booking from Booking booking" +
            " where booking.booker.id = ?1" +
            " and booking.start <= ?2" +
            " and booking.end >= ?2")
    Page<Booking> findCurrentByBookerId(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusEquals(Long bookerId, Status status, Pageable pageable);

    Page<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    @Query("select booking from Booking booking" +
            " where booking.item.owner.id = ?1" +
            " and booking.start <= ?2" +
            " and booking.end >= ?2")
    Page<Booking> findCurrentBookingsByOwnerId(Long ownerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStatusEquals(Long bookerId, Status status, Pageable pageable);
}