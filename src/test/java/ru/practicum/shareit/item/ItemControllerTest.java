package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.converter.CommentConverter;
import ru.practicum.shareit.item.converter.ItemConverter;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @MockBean
    private CommentConverter commentConverter;
    @MockBean
    private ItemConverter itemConverter;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    private final int from = 0;
    private final int size = 10;
    private final String[] sortBy = new String[] {"start;DESC"};

    private final User owner = User.builder().id(1L).name("owner").email("owner@email.ru").build();
    private final User booker = User.builder().id(6L).name("booker").email("booker@email.ru").build();
    private final Item item = Item.builder().id(3L).description("iDesc").owner(owner).build();
    private final BookingDto lastBooking = BookingDto.builder().id(8L).bookerId(booker.getId()).build();
    private final BookingDto nextBooking = BookingDto.builder().id(9L).bookerId(booker.getId()).build();
    private final ItemWithBookings itemWithBookings = ItemWithBookings.builder().id(3L).lastBooking(lastBooking).nextBooking(nextBooking).build();
    private final ItemRequest itemRequest = ItemRequest.builder().id(4L).description("needItem").requestor(booker).build();
    private final ItemDto itemDto = ItemDto.builder().id(3L).available(true).name("iName").description("iDescription").requestId(itemRequest.getId()).build();
    private final Comment comment = Comment.builder().id(7L).text("iComment").item(item).author(booker).build();

    @Test
    @DisplayName("GET get returns items and status 200 Ok")
    void getItemTest() throws Exception {
        when(itemConverter.convertToItemWithBookings(item)).thenReturn(itemWithBookings);
        when(itemService.getItems(booker.getId(), from, size)).thenReturn(List.of(itemWithBookings));

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemWithBookings))
                        .queryParam("from", String.valueOf(from))
                        .queryParam("size", String.valueOf(size))
                                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemWithBookings.getId().intValue())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemWithBookings.getLastBooking().getId().intValue())));
    }

    @Test
    @DisplayName("GET getById returns item and status 200 Ok")
    void getByIdTest() throws Exception {
        when(itemConverter.convertToItemWithBookings(item)).thenReturn(itemWithBookings);
        when(itemService.getById(itemWithBookings.getId(), booker.getId())).thenReturn(itemWithBookings);

        mvc.perform(get("/items/3")
                        .content(mapper.writeValueAsString(itemWithBookings))
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id", is(itemWithBookings.getId().intValue())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemWithBookings.getLastBooking().getId().intValue())));
    }

    @Test
    @DisplayName("GET getById returns NotFoundException and status 404")
    void getByIdReturnsNotFoundExTest() throws Exception {
        when(itemConverter.convertToItemWithBookings(item)).thenReturn(itemWithBookings);
        when(itemService.getById(100L, booker.getId())).thenThrow(NotFoundException.class);

        mvc.perform(get("/items/100")
                        .content(mapper.writeValueAsString(itemWithBookings))
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("POST create returns itemDto and status 200 Ok")
    void createItemTest() throws Exception {
        when(itemService.create(1L, itemDto)).thenReturn(item);
        when(itemConverter.convert(item)).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId().intValue())));
    }

    @Test
    @DisplayName("PATCH update returns itemDto and status 200 Ok")
    void updateItemTest() throws Exception {
        ItemDto newItem = ItemDto.builder().id(3L).description("iDescription").build();
        when(itemConverter.convert(item)).thenReturn(newItem);
        when(itemService.update(owner.getId(), newItem.getId(), newItem)).thenReturn(item);

        mvc.perform(patch("/items/3")
                        .content(mapper.writeValueAsString(newItem))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newItem.getId().intValue())))
                .andExpect(jsonPath("$.description", is("iDescription")));
    }

    @Test
    @DisplayName("PATCH update returns NotFoundException and status 404")
    void updateItemReturnsNotFoundExceptionTest() throws Exception {
        ItemDto newItem = ItemDto.builder().id(3L).description("iDescription").build();
        when(itemConverter.convert(item)).thenThrow(NotFoundException.class);
        when(itemService.update(100L, newItem.getId(), newItem)).thenReturn(item);

        mvc.perform(patch("/items/3")
                        .content(mapper.writeValueAsString(newItem))
                        .header(USER_ID_HEADER, 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("GET search items returns items and status 200 Ok")
    void searchItemTest() throws Exception {
        when(itemConverter.convert(item)).thenReturn(itemDto);
        when(itemService.searchItems("descr", from, size)).thenReturn(List.of(item));

        mvc.perform(get("/items/search")
                        .content(mapper.writeValueAsString(itemDto))
                        .queryParam("text", "descr")
                        .queryParam("from", String.valueOf(from))
                        .queryParam("size", String.valueOf(size))
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId().intValue())));
    }

    @Test
    @DisplayName("GET search items with empty text returns ValidationException and status 400")
    void searchItemReturnsValidationExTest() throws Exception {
        when(itemConverter.convert(item)).thenReturn(itemDto);
        when(itemService.searchItems("", from, size)).thenThrow(ValidationException.class);

        mvc.perform(get("/items/search")
                        .content(mapper.writeValueAsString(itemDto))
                        .queryParam("text", "")
                        .queryParam("from", String.valueOf(from))
                        .queryParam("size", String.valueOf(size))
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("POST addComment returns commentDto and status 200 Ok")
    void addCommentTest() throws Exception {
        CommentDto commentDto = CommentDto.builder().id(7L).created(LocalDateTime.now())
                .text("iComment").authorName("booker").build();
        when(commentConverter.convert(comment)).thenReturn(commentDto);
        when(itemService.addComment(any(), anyLong(), anyLong())).thenReturn(comment);

        mvc.perform(post("/items/3/comment")
                .content(mapper.writeValueAsString(commentDto))
                .header(USER_ID_HEADER, 6L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId().intValue())))
                .andExpect(jsonPath("$.text", is("iComment")));
    }
}