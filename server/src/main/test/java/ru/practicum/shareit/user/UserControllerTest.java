package ru.practicum.shareit.user;

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
import ru.practicum.shareit.dto.UserDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.converter.UserConverter;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @MockBean
    private UserService userService;
    @MockBean
    private UserConverter userConverter;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    private final User user = User.builder().id(1L).name("u").email("user@email.ru").build();
    private final UserDto userDto = UserDto.builder().id(1L).name("u").email("user@email.ru").build();

    @Test
    @DisplayName("GET getAllUsers returns users and status 200 Ok")
    void getAllUsersTest() throws Exception {
        when(userConverter.convert(user)).thenReturn(userDto);
        when(userService.getAllUsers()).thenReturn(List.of(user));

        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())));
    }

    @Test
    @DisplayName("GET getById returns user and status 200 Ok")
    void getUserByIdTest() throws Exception {
        when(userConverter.convert(user)).thenReturn(userDto);
        when(userService.getById(1L)).thenReturn(user);

        mvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @Test
    @DisplayName("GET getById returns NotFoundException and status 404")
    void getUserByIdReturnsNotFoundExceptionTest() throws Exception {
        when(userConverter.convert(user)).thenReturn(userDto);
        when(userService.getById(1L)).thenThrow(NotFoundException.class);

        mvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("POST create returns user and status 200 Ok")
    void createUserTest() throws Exception {
        when(userService.create(userDto)).thenReturn(user);
        when(userConverter.convert(user)).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @Test
    @DisplayName("PATCH update returns user and status 200 Ok")
    void updateUserTest() throws Exception {
        when(userConverter.convert(user)).thenReturn(userDto);
        when(userService.update(anyLong(), any())).thenReturn(user);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @Test
    @DisplayName("PATCH update returns NotFoundException and status 404")
    void updateUserReturnsNotFoundExceptionTest() throws Exception {
        when(userConverter.convert(user)).thenReturn(userDto);
        when(userService.update(anyLong(), any())).thenThrow(NotFoundException.class);

        mvc.perform(patch("/users/10")
                        .content(mapper.writeValueAsString(userDto))
                        .header(USER_ID_HEADER, 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }
}