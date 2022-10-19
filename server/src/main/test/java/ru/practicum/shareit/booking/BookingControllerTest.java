package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.State;
import ru.practicum.shareit.Status;
import ru.practicum.shareit.booking.converter.BookingConverter;
import ru.practicum.shareit.dto.IncomingBookingDto;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.StateValidationException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Status.APPROVED;
import static ru.practicum.shareit.Status.WAITING;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @MockBean
    private BookingService bookingService;
    @MockBean
    private BookingConverter bookingConverter;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    private final int from = 0;
    private final int size = 10;
    private final String[] sortBy = new String[] {"start;DESC"};

    private final User owner = User.builder().id(1L).name("owner").email("owner@email.ru").build();
    private final Item item = Item.builder().id(6L).available(true).description("item").owner(owner).build();
    private final IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
            .id(5L)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .itemId(item.getId()).build();
    private final User booker = User.builder().id(2L).name("booker").email("booker@email.ru").build();
    private final Booking booking = Booking.builder()
            .id(5L)
            .start(incomingBookingDto.getStart())
            .end(incomingBookingDto.getEnd())
            .item(item)
            .booker(booker)
            .status(WAITING)
            .build();

    @Test
    @DisplayName("POST Create with status 200 Ok")
    void createAndReturnIncomingBookingDtoTest() throws Exception {
        when(bookingConverter.convert(incomingBookingDto)).thenReturn(booking);
        when(bookingService.create(booker.getId(),incomingBookingDto)).thenReturn(booking);

        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(incomingBookingDto))
                .header(USER_ID_HEADER, booker.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId().intValue())))
                .andExpect(jsonPath("$.start", is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId().intValue())));
    }

    @Test
    @DisplayName("POST Create booking throws validation exception when end date is before start and returns status 400")
    void createBookingThrowsValidationExTest() throws Exception {
        IncomingBookingDto bookingDto = IncomingBookingDto.builder()
                .id(5L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now())
                .itemId(item.getId()).build();
        when(bookingConverter.convert(incomingBookingDto)).thenReturn(booking);
        when(bookingService.create(booker.getId(),bookingDto)).thenThrow(ValidationException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("PATCH Process Request whith status 200 Ok")
    void processRequestTest() throws Exception {
        Booking bookingApproved = booking.toBuilder().status(Status.APPROVED).build();
        when(bookingService.processRequest(owner.getId(),booking.getId(),true))
                .thenReturn(bookingApproved);

        mvc.perform(patch("/bookings/5")
                .content(mapper.writeValueAsString(bookingApproved))
                .queryParam("approved", "true")
                .header(USER_ID_HEADER, owner.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingApproved.getId().intValue())))
                .andExpect(jsonPath("$.status", is(APPROVED.name())));
        }


    @Test
    void processRequestThrowsNotAvailableExTest() throws Exception {
        Item itemNotAvailable = item.toBuilder().available(false).build();
        Booking bookingFailed = booking.toBuilder().item(itemNotAvailable).build();
        when(bookingService.processRequest(owner.getId(),booking.getId(),true))
                .thenThrow(NotAvailableException.class);

        mvc.perform(patch("/bookings/5")
                        .content(mapper.writeValueAsString(bookingFailed))
                        .queryParam("approved", "true")
                        .header(USER_ID_HEADER, owner.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET GetById returns booking and status 200 Ok")
    void getBookingByIdTest() throws Exception {
        when(bookingService.getById(booking.getId(), booker.getId())).thenReturn(booking);

        mvc.perform(get("/bookings/5")
                        .content(mapper.writeValueAsString(booking))
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId().intValue())))
                .andExpect(jsonPath("$.start", is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId().intValue())));
    }

    @Test
    void getBookingByIdReturnsNotFoundEx() throws Exception {
        when(bookingService.getById(booking.getId(), 10L)).thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings/5")
                        .content(mapper.writeValueAsString(booking))
                        .header(USER_ID_HEADER, 10)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("GET getAllByOwner returns bookings and status 200 Ok")
    void getAllBookingsByOwnerTest() throws Exception {
        when(bookingService.getAllByOwner(owner.getId(), State.ALL.name(), from, size, sortBy))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_HEADER, 1L)
                        .queryParam("state", "ALL")
                        .queryParam("from", String.valueOf(from))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sortBy", "start;DESC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId().intValue())))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @DisplayName("GET getAllByOwner returns NotFoundException and status 404")
    void getAllBookingsByOwnerThrowsExTest() throws Exception {
        when(bookingService.getAllByOwner(10L, State.ALL.name(), from, size, sortBy))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_HEADER, 10L)
                        .queryParam("state", "ALL")
                        .queryParam("from", String.valueOf(from))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sortBy", "start;DESC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("GET getAllByBooker returns bookings and status 200 Ok")
    void getAllBookingsByBookerTest() throws Exception {
        when(bookingService.getAllByBooker(booker.getId(), State.WAITING.name(), from, size, sortBy))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_HEADER, 2L)
                        .queryParam("state", "WAITING")
                        .queryParam("from", String.valueOf(from))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sortBy", "start;DESC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId().intValue())))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @DisplayName("GET getAllByBooker returns StateValidationException and status 400")
    void getAllBookingsByBookerThrowsExTest() throws Exception {
        when(bookingService.getAllByBooker(booker.getId(), "WRONGSTATE", from, size, sortBy))
                .thenThrow(StateValidationException.class);

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_HEADER, 2L)
                        .queryParam("state", "WRONGSTATE")
                        .queryParam("from", String.valueOf(from))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sortBy", "start;DESC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }
 }