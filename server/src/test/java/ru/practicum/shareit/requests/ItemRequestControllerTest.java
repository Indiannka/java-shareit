package ru.practicum.shareit.requests;

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
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.requests.converter.RequestConverter;
import ru.practicum.shareit.user.User;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private RequestConverter requestConverter;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    private final int from = 0;
    private final int size = 10;
    private final String[] sortBy = new String[] {"created;ASC"};

    private final User owner = User.builder().id(1L).name("owner").email("owner@email.ru").build();
    private final User requestor = User.builder().id(2L).name("requestor").email("requestorr@email.ru").build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(4L).description("smth").created(LocalDateTime.now()).build();
    private final ItemRequest itemRequest = ItemRequest.builder().id(4L).description("smth").requestor(requestor).created(itemRequestDto.getCreated()).build();

    @Test
    @DisplayName("POST create item request returns ItemRequest and status 200 Ok")
    void createItemRequestTest() throws Exception {
        when(requestConverter.convert(itemRequestDto)).thenReturn(itemRequest);
        when(itemRequestService.create(2L, itemRequestDto)).thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequest))
                        .header(USER_ID_HEADER, 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId().intValue())))
                .andExpect(jsonPath("$.requestor.id", is(itemRequest.getRequestor().getId().intValue())));
    }

    @Test
    @DisplayName("GET getAllUserRequests by Requestor returns ItemRequestDto and status 200 Ok")
    void getAllUserRequestsByRequestorTest() throws Exception {
        when(requestConverter.convert(itemRequest)).thenReturn(itemRequestDto);
        when(itemRequestService.getAllUserRequests(2L, sortBy)).thenReturn(List.of(itemRequest));

        mvc.perform(get("/requests")
                .content(mapper.writeValueAsString(itemRequestDto))
                .queryParam("sortBy", "created;ASC")
                .header(USER_ID_HEADER, 2L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    @DisplayName("GET getAllUserRequests returns NotFoundException when user is wrong and status 404")
    void getAllUserRequestsByRequestorReturnsNotFoundExceptionTest() throws Exception {
        when(requestConverter.convert(itemRequest)).thenReturn(itemRequestDto);
        when(itemRequestService.getAllUserRequests(10L, sortBy)).thenThrow(NotFoundException.class);

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .queryParam("sortBy", "created;ASC")
                        .header(USER_ID_HEADER, 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("GET getAllUserRequests returns ItemRequestDto and status 200 Ok")
    void getAllUserRequestsTest() throws Exception {
        when(itemRequestService.getAll(1L,from, size, sortBy)).thenReturn(List.of(itemRequest));
        when(requestConverter.convert(itemRequest)).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .queryParam("from", String.valueOf(from))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sortBy", "created;ASC")
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    @DisplayName("GET getAllUserRequests returns returns NotFoundException when user is wrong and status 404")
    void getAllUserRequestsReturnsNotFoundExceptionTest() throws Exception {
        when(requestConverter.convert(itemRequest)).thenReturn(itemRequestDto);
        when(itemRequestService.getAll(1L,from, size, sortBy)).thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .queryParam("from", String.valueOf(from))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sortBy", "created;ASC")
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("GET getAllUserRequests returns returns ConstraintViolationException when request parameter" +
                 " is wrong and status 400")
    void getAllUserRequestsReturnsConstraintViolationExceptionTest() throws Exception {
        when(itemRequestService.getAll(1L,-1, size, sortBy)).thenThrow(ConstraintViolationException.class);

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .queryParam("from", String.valueOf(-1))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sortBy", "created;ASC")
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("GET getById returns ItemRequestDto and status 200 Ok")
    void getItemRequestByIdTest() throws Exception {
        when(requestConverter.convert(itemRequest)).thenReturn(itemRequestDto);
        when(itemRequestService.getById(4L, 2L)).thenReturn(itemRequest);

        mvc.perform(get("/requests/4")
                .content(mapper.writeValueAsString(itemRequestDto))
                .header(USER_ID_HEADER, 2L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId().intValue())));
    }
}