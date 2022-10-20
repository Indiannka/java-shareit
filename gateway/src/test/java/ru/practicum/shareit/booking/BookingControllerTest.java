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
import ru.practicum.shareit.dto.IncomingBookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @Test
    @DisplayName("POST create booking fails when endBookingDate before startBookingDate")
    void checkDateRangeValidationFailsOnCreate() throws Exception {

        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .id(5L)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(2L).build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(incomingBookingDto))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("POST create booking fails when endBookingDate equals startBookingDate")
    void checkDateRangeValidationOnCreateWithEqualDates() throws Exception {
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .id(5L)
                .start(LocalDateTime.of(2022,10,10,15,15))
                .end(LocalDateTime.of(2022,10,10,15,15))
                .itemId(2L).build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(incomingBookingDto))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }
}