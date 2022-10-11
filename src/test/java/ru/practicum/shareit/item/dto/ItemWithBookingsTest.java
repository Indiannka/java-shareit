package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemWithBookingsTest {

    @Autowired
    private JacksonTester<ItemWithBookings> json;

    private final User booker = User.builder().id(2L).name("booker").email("booker@email.ru").build();
    private final CommentDto comment = CommentDto.builder().id(7L).text("iComment").authorName(booker.getName()).build();
    private final BookingDto booking = BookingDto.builder()
            .id(4L)
            .start(LocalDateTime.now().minusDays(2))
            .end(LocalDateTime.now().minusDays(1))
            .bookerId(booker.getId())
            .build();
    private final BookingDto secondBooking = BookingDto.builder()
            .id(5L)
            .start(LocalDateTime.now().plusMinutes(5))
            .end(LocalDateTime.now().plusDays(1))
            .bookerId(booker.getId())
            .build();

    @Test
    void itemWithBookingsJsonTest() throws IOException {
        ItemWithBookings itemWithBookings = ItemWithBookings.builder()
                .id(6L)
                .available(true)
                .name("smth")
                .description("item")
                .lastBooking(booking)
                .nextBooking(secondBooking)
                .comments(Set.of(comment))
                .build();

        JsonContent<ItemWithBookings> result = json.write(itemWithBookings);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(6);
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("smth");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(4);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("iComment");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("booker");
    }
}