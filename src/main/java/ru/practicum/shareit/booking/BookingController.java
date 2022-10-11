package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.converter.BookingConverter;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.config.Create;

import javax.validation.constraints.Min;
import java.util.Collection;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;
    private final BookingConverter bookingConverter;

    @PostMapping
    public IncomingBookingDto create(@RequestHeader(USER_ID_HEADER) long userId,
                                     @Validated({Create.class})
                                     @RequestBody IncomingBookingDto incomingBookingDto) {
        log.info("POST request: добавление бронирования {} пользователем с id {}", incomingBookingDto, userId);
        return bookingConverter.convertToIncomingDto(bookingService.create(userId, incomingBookingDto));
    }

    @PatchMapping("/{bookingId}")
    public Booking processRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                  @PathVariable("bookingId") long bookingId,
                                  @RequestParam("approved") Boolean approval) {
        log.info("PATCH request: подтверждение бронирования {} пользователем с id {}", bookingId, userId);
        return bookingService.processRequest(userId, bookingId, approval);
    }

    @GetMapping("/{bookingId}")
    public Booking getById(@RequestHeader(USER_ID_HEADER) long userId,
                           @PathVariable("bookingId") long bookingId) {
        log.info("GET request: запрос бронирования id {}, пользователем id {} ", bookingId, userId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping("/owner")
    public Collection<Booking> getAllByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state,
                                             @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
                                             @RequestParam(defaultValue = "10", required = false) @Min(1) int size,
                                             @RequestParam(value = "sort", defaultValue = "start;DESC",
                                                     required = false) String[] sortBy) {
        log.info("GET request: запрос списка бронирований, владельцем предмета id {} ", userId);
        return bookingService.getAllByOwner(userId, state, from, size, sortBy);
    }

    @GetMapping
    public Collection<Booking> getAllByBooker(@RequestHeader(USER_ID_HEADER) long userId,
                                              @RequestParam(defaultValue = "ALL", required = false) String state,
                                              @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
                                              @RequestParam(defaultValue = "10", required = false) @Min(1) int size,
                                              @RequestParam(value = "sort", defaultValue = "start;DESC",
                                                      required = false) String[] sortBy) {
        log.info("GET request: запрос списка бронирований, пользователем id {} ", userId);
        return bookingService.getAllByBooker(userId, state, from, size, sortBy);
    }
}