package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.Status;
import ru.practicum.shareit.dto.IncomingBookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class IncomingBookingDtoTest {

    @Autowired
    private JacksonTester<IncomingBookingDto> json;

    @Test
    void testIncomingBookingDto() throws Exception {
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder().id(1L)
                .start(LocalDateTime.of(2022,9,01, 19, 00, 05))
                .end(LocalDateTime.of(2022,10,01, 19, 00, 03))
                .status(Status.WAITING)
                .build();

        JsonContent<IncomingBookingDto> result = json.write(incomingBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-09-01T19:00:05");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-10-01T19:00:03");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}