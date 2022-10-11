package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.converter.CommentConverter;
import ru.practicum.shareit.item.converter.ItemConverter;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImp serviceImpl;

    @Mock
    private ItemConverter itemConverter;
    @Mock
    private CommentConverter commentConverter;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private final User owner = User.builder().id(1L).name("owner").email("owner@email.ru").build();
    private final User booker = User.builder().id(6L).name("booker").email("booker@email.ru").build();
    private final Item item = Item.builder().id(3L).description("iDescription").owner(owner).build();
    private final BookingDto lastBooking = BookingDto.builder().id(8L).bookerId(booker.getId()).build();
    private final BookingDto nextBooking = BookingDto.builder().id(9L).bookerId(booker.getId()).build();
    private final ItemWithBookings itemWithBookings = ItemWithBookings.builder().id(3L).lastBooking(lastBooking).nextBooking(nextBooking).build();
    private final ItemRequest itemRequest = ItemRequest.builder().id(4L).description("needItem").requestor(booker).build();
    private final ItemDto itemDto = ItemDto.builder().available(true).description("newDescr").name("iName").requestId(itemRequest.getId()).build();
    private final Comment comment = Comment.builder().id(7L).text("iComment").item(item).author(booker).build();
    private final Booking booking = Booking.builder().id(5L).booker(booker).item(item).build();

    @Test
    void createItemTest() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemConverter.convert(itemDto)).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);

        Item item = serviceImpl.create(1L, itemDto);

        assertEquals(owner, item.getOwner());
        assertEquals(itemRequest, item.getRequest());
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Check create method throws NotFoundException when empty itemDto")
    void createItemFailTest() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemConverter.convert(itemDto)).thenReturn(null);
        final var thrown = assertThrows(
                    NotFoundException.class, () -> serviceImpl.create(owner.getId(), itemDto));
            assertEquals("Отсутствуют параметры входящего объекта itemDto", thrown.getMessage());
    }

    @Test
    @DisplayName("Check create method throws NotFoundException when wrong user")
    void createItemThrowsNotFoundExceptionTest() {
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.create(10L, itemDto));
        assertEquals("Пользователь № 10 не найден", thrown.getMessage());
        verify(itemRepository,never()).findById(10L);
    }

    @Test
    void updateItemTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        Item item = serviceImpl.update(1L, 3L, itemDto);
        assertEquals("newDescr", item.getDescription());
        assertEquals("iName", item.getName());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Check update method throws NotFoundException when wrong user")
    void updateItemThrowsNotFoundExceptionTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.update(10L,3L, itemDto));
        assertEquals("Пользователь № 10 не является владельцем предмета", thrown.getMessage());
        verify(itemRepository,never()).save(item);
    }

    @Test
    void searchItemsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        when(itemRepository.searchItems(anyString(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(new Item())));
        String text = "iDescription";
        serviceImpl.searchItems(text, 0, 10);
        verify(itemRepository, times(1)).searchItems(anyString(),any());
    }

    @Test
    void getItemsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        CommentDto commentDto = null;
        when(itemRepository.findAllByOwnerId(anyLong(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(itemConverter.convertToItemWithBookings(item)).thenReturn(itemWithBookings);
        when(commentRepository.findAllByItemId(3L)).thenReturn(List.of(comment));
        when(commentConverter.convert(comment)).thenReturn(commentDto.builder()
                .id(comment.getId()).text(comment.getText()).build());

        List<ItemWithBookings> items = (List<ItemWithBookings>) serviceImpl.getItems(1L, 0,10);
        assertEquals(lastBooking, (items.get(0).getLastBooking()));
        assertEquals(nextBooking, (items.get(0).getNextBooking()));
        assertNotNull(items.get(0).getComments());
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(),any());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());
    }

    @Test
    void addCommentTest() {
        CommentDto commentDto = CommentDto.builder().id(comment.getId())
                .text(comment.getText()).authorName(booker.getName()).build();
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndStartIsBefore(anyLong(),anyLong(),any()))
                .thenReturn(Optional.of(booking));
        when(commentConverter.convert(commentDto)).thenReturn(comment);

        serviceImpl.addComment(commentDto,6L,3L);
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Check addComment method throws ValidationException when wrong user")
    void addCommentFailExTest() {
        CommentDto commentDto = CommentDto.builder().id(comment.getId())
                .text(comment.getText()).authorName(booker.getName()).build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        final var thrown = assertThrows(
                ValidationException.class, () -> serviceImpl.addComment(commentDto,1L,3L));
        assertEquals("Предмет № 3 не найден у пользователя № 1", thrown.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Check addComment method throws NotFoundException when wrong user")
    void addCommentThrowsExTest() {
        CommentDto commentDto = CommentDto.builder().id(comment.getId())
                .text("").authorName(booker.getName()).build();
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.addComment(commentDto,100L,3L));
        assertEquals("Пользователь № 100 не найден", thrown.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addCommentFailTest() {
        CommentDto commentDto = CommentDto.builder().id(comment.getId())
                .text(comment.getText()).authorName(booker.getName()).build();
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndStartIsBefore(anyLong(),anyLong(),any()))
                .thenReturn(Optional.of(booking));
        when(commentConverter.convert(commentDto)).thenReturn(null);

        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.addComment(commentDto,booker.getId(),3L));
        assertEquals("Отсутствуют параметры входящего объекта commentDTO " + commentDto, thrown.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getItemByIdTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemConverter.convertToItemWithBookings(item)).thenReturn(itemWithBookings);

        serviceImpl.getById(item.getId(), owner.getId());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getItemByIdItemAndUserFailTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemConverter.convertToItemWithBookings(item)).thenReturn(null);
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.getById(item.getId(),booker.getId()));
        assertEquals("Отсутствуют параметры конвертируемого объекта item " + item.getId(), thrown.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getItemByIdItemFailTest() {
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImpl.getById(100L));
        assertEquals("Предмет № 100 не найден", thrown.getMessage());
        verify(commentRepository, never()).save(any());
    }
}