package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.converter.BookingConverter;
import ru.practicum.shareit.dto.IncomingBookingDto;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;
    private final BookingConverter bookingConverter;

    @PostMapping
    public Booking create(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @RequestBody IncomingBookingDto incomingBookingDto) {
        return bookingService.create(userId, incomingBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking processRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @PathVariable("bookingId") Long bookingId,
                                  @RequestParam("approved") boolean approved) {
        return bookingService.processRequest(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getById(@RequestHeader(USER_ID_HEADER) Long userId,
                           @PathVariable("bookingId") Long bookingId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping("/owner")
    public Collection<Booking> getAllByOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @RequestParam String state,
                                             @RequestParam int from,
                                             @RequestParam int size,
                                             @RequestParam String[] sortBy) {
        return bookingService.getAllByOwner(userId, state, from, size, sortBy);
    }

    @GetMapping
    public Collection<Booking> getAllByBooker(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @RequestParam String state,
                                              @RequestParam int from,
                                              @RequestParam int size,
                                              @RequestParam String[] sortBy) {
        return bookingService.getAllByBooker(userId, state, from, size, sortBy);
    }
}