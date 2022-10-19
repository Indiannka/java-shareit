package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Create;
import ru.practicum.shareit.dto.IncomingBookingDto;

import javax.validation.constraints.Min;


@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
									 @Validated({Create.class})
									 @RequestBody IncomingBookingDto incomingBookingDto) {
		log.info("POST request: добавление бронирования {} пользователем с id {}", incomingBookingDto, userId);
		return bookingClient.create(userId, incomingBookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> processRequest(@RequestHeader(USER_ID_HEADER) Long userId,
								  @PathVariable("bookingId") Long bookingId,
								  @RequestParam("approved") boolean approved) {
		log.info("PATCH request: подтверждение бронирования {} пользователем с id {}", bookingId, userId);
		return bookingClient.processRequest(userId, bookingId, approved);
	}

	@GetMapping
	public ResponseEntity<Object> getAllByBooker(@RequestHeader(USER_ID_HEADER) Long userId,
											  @RequestParam(defaultValue = "ALL", required = false) String state,
											  @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
											  @RequestParam(defaultValue = "10", required = false) @Min(1) int size,
											  @RequestParam(defaultValue = "start;DESC",
													  required = false) String[] sortBy) {
		log.info("GET request: запрос списка бронирований, пользователем id {} ", userId);
		return bookingClient.getAllByBooker(userId, state, from, size, sortBy);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllByOwner(@RequestHeader(USER_ID_HEADER) Long userId,
											 @RequestParam(defaultValue = "ALL", required = false) String state,
											 @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
											 @RequestParam(defaultValue = "10", required = false) @Min(1) int size,
											 @RequestParam(defaultValue = "start;DESC",
													 required = false) String[] sortBy) {
		log.info("GET request: запрос списка бронирований, владельцем предмета id {} ", userId);
		return bookingClient.getAllByOwner(userId, state, from, size, sortBy);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) Long userId,
						                  @PathVariable("bookingId") Long bookingId) {
		log.info("GET request: запрос бронирования id {}, пользователем id {} ", bookingId, userId);
		return bookingClient.getById(userId, bookingId);
	}
}