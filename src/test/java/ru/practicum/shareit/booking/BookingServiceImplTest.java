package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.converter.BookingConverter;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.Status.REJECTED;
import static ru.practicum.shareit.booking.Status.WAITING;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImp serviceImp;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingConverter bookingConverter;

    private final int page = 0;
    private final int size = 10;
    private final String[] sortBy = new String[] {"start;DESC"};

    private final User owner = User.builder().id(1L).name("owner").email("owner@email.ru").build();
    private final User booker = User.builder().id(2L).name("booker").email("booker@email.ru").build();
    private final User otherUser = User.builder().id(3L).name("otherUser").email("otherUser@email.ru").build();
    private final Item item = Item.builder().id(6L).available(true).description("item").owner(owner).build();
    private final IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusDays(1))
            .itemId(item.getId()).build();
    private final Booking booking = Booking.builder()
            .id(5L)
            .start(incomingBookingDto.getStart())
            .end(incomingBookingDto.getEnd())
            .item(item)
            .booker(booker)
            .status(WAITING)
            .build();

    @Test
    @DisplayName("Check Booking create method calls repository methods")
    void createBookingTest() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingConverter.convert(incomingBookingDto)).thenReturn(booking);

        serviceImp.create(booker.getId(), incomingBookingDto);

        verify(userRepository, times(1)).findById(2L);
        verify(itemRepository, atLeast(1)).findById(6L);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Check Booking create method fails if owner try to book item")
    void createBookingFailTest() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingConverter.convert(incomingBookingDto)).thenReturn(booking);

        final var thrown = assertThrows(NotFoundException.class, () -> serviceImp.create(owner.getId(), incomingBookingDto));
        assertEquals("Пользователь не может бронировать свои предметы", thrown.getMessage());
    }

    @Test
    @DisplayName("Check NotFoundException is thrown if user not found")
    void createBookingThrowsNotFoundExceptionTest() {
        final var thrown = assertThrows(NotFoundException.class, () -> serviceImp.create(10L, incomingBookingDto));
        assertEquals("Пользователь № 10 не найден", thrown.getMessage());
    }

    @Test
    @DisplayName("Check NotFoundException is thrown if item not found")
    void createBookingThrowsNotFoundExceptionForItemTest() {
        IncomingBookingDto bookingDto = IncomingBookingDto.builder().itemId(100L).build();
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        final var thrown = assertThrows(NotFoundException.class, () -> serviceImp.create(booker.getId(), bookingDto));
        assertEquals("Предмет № 100 не найден", thrown.getMessage());
    }

    @Test
    @DisplayName("Check NotAvailableException is thrown")
    void checkIfMethodIsAvailableThrowsExTest() {
        Item item = Item.builder().id(1L).available(false).description("item").owner(owner).build();
        final var ex = new NotAvailableException("Предмет недоступен для бронирования в данный момент");
        final var thrown = assertThrows(NotAvailableException.class, () -> serviceImp.isAvailable(item));
        assertSame(ex.getMessage(), thrown.getMessage());
    }

    @Test
    @DisplayName("Check Validation ex is thrown when booking dates are incorrect")
    void checkBookingDatesThrowsExTest() {
        IncomingBookingDto bookingDto = IncomingBookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().minusDays(1))
                .itemId(item.getId()).build();
        final var ex = new ValidationException("Дата окончания бронирования должна быть позже даты начала бронирования.");
        final var thrown = assertThrows(ValidationException.class, () -> serviceImp.checkBookingDates(bookingDto));
        assertSame(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void methodReturnUserTypesTest() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        UserType booker = serviceImp.getUserType(booking, booking.getBooker().getId());
        assertEquals(UserType.BOOKER, booker);

        UserType owner = serviceImp.getUserType(booking, item.getOwner().getId());
        assertEquals(UserType.OWNER, owner);

        UserType other = serviceImp.getUserType(booking, -1L);
        assertEquals(UserType.OTHER_USER, other);
    }

    @Test
    @DisplayName("Check getUserType throws NotFoundException when user is wrong")
    void checkReturnUserTypeFailTest() {
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImp.getUserType(booking, 5L));
        assertEquals("Предмет № 6 не найден", thrown.getMessage());
    }

    @Test
    @DisplayName("Check Booking getById method calls repository methods")
    void getByIdTest() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Booking returnedBooking = serviceImp.getById(booking.getId(), booker.getId());

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(booking.getId());
        assertEquals(returnedBooking,booking);
    }

    @Test
    @DisplayName("Check Booking getById throws NotFoundException when userId is not one of OWNER or BOOKER")
    void getByIdFailTest() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.ofNullable(otherUser));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImp.getById(booking.getId(), otherUser.getId()));
        assertEquals("Пользователь № 3 не является владельцем вещи или заказа", thrown.getMessage());
    }

    @Test
    @DisplayName("Check Booking getById throws NotFoundException when userId is wrong")
    void getByIdFailThrowsNotFoundExceptionTest() {
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImp.getById(booking.getId(), 10L));
        assertEquals("Пользователь № 10 не найден", thrown.getMessage());
    }

    @Test
    @DisplayName("Check Booking getById throws NotFoundException when userId is not one of OWNER or BOOKER")
    void getByIdFailNotFoundExForItemTest() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.ofNullable(otherUser));
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImp.getById(10L, otherUser.getId()));
        assertEquals("Бронирование № 10 не найдено", thrown.getMessage());
    }

    @Test
    @DisplayName("Check that Booking  status is processed correctly to APPROVED by itemOwner")
    void processRequestTest() {
        Booking processedRequest = Booking.builder()
                .id(7L)
                .item(item)
                .booker(booker)
                .status(WAITING)
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findById(processedRequest.getId())).thenReturn(Optional.of(processedRequest));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        serviceImp.processRequest(owner.getId(),processedRequest.getId(),true);
        assertEquals(Status.APPROVED, processedRequest.getStatus());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Check that Booking  status is processed with StatusProcessException when booking status is REJECTED")
    void processRequestFailOnBookingStatusTest() {
        Booking processedRequest = Booking.builder()
                .id(7L)
                .item(item)
                .booker(booker)
                .status(REJECTED)
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findById(processedRequest.getId())).thenReturn(Optional.of(processedRequest));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        final var thrown = assertThrows(
                StatusProcessException.class, () -> serviceImp.processRequest(owner.getId(),processedRequest.getId(),true));
        assertEquals("Статус заказа уже переведен в REJECTED", thrown.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Check that method processRequest throws NotFoundException if user is not found")
    void processRequestThrowsNotFoundExceptionTest() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class, () -> serviceImp.processRequest(10L,booking.getId(),true));
        assertEquals("Пользователь № 10 не найден", thrown.getMessage());
    }

    @Test
    @DisplayName("Check that method processRequest throws NotFoundException if booking is not found")
    void processRequestThrowsNotFoundExceptionForBookingTest() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.ofNullable(owner));
        NotFoundException thrown = assertThrows(
                NotFoundException.class, () -> serviceImp.processRequest(owner.getId(), 10L,true));
        assertEquals("Бронирование № 10 не найдено", thrown.getMessage());
    }

    @Test
    @DisplayName("Check that method processRequest throws NotFoundException when request is not from ItemOwner")
    void processRequestFailsWhenNotItemOwnerRequestTest() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.ofNullable(otherUser));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        NotFoundException thrown = assertThrows(
                NotFoundException.class, () -> serviceImp.processRequest(otherUser.getId(),booking.getId(),true));
        assertEquals("Пользователь № 3 не является владельцем вещи", thrown.getMessage());
    }

    @Test
    void getAllBookingsByOwnerTest() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        State argsState = State.ALL;
        when(bookingRepository.findByItemOwnerId(anyLong(), any())).thenReturn(new PageImpl<>(List.of(new Booking())));

        Collection<Booking> bookings = serviceImp.getAllByOwner(1L, argsState.name(), page, size, sortBy);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findByItemOwnerId(anyLong(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllPastBookingsByOwnerTest() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        State argsState = State.PAST;
        long userId = booking.getItem().getOwner().getId();
        when(bookingRepository.findByItemOwnerIdAndEndIsBefore(eq(userId), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));

        Collection<Booking> bookings = serviceImp.getAllByOwner(userId, argsState.name(), page, size, sortBy);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndEndIsBefore(anyLong(), any(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllCurrentBookingsByOwnerTest() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        State argsState = State.CURRENT;
        when(bookingRepository.findCurrentBookingsByOwnerId(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));

        Collection<Booking> bookings = serviceImp.getAllByOwner(1L, argsState.name(), page, size, sortBy);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findCurrentBookingsByOwnerId(anyLong(), any(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllBookingsWithRejectedStateByOwnerTest() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        State argsState = State.REJECTED;
        when(bookingRepository.findByItemOwnerIdAndStatusEquals(anyLong(), eq(REJECTED), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));

        Collection<Booking> bookings = serviceImp.getAllByOwner(1L, argsState.name(), page, size, sortBy);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStatusEquals(anyLong(), any(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllBookingsWithWaitingStateByOwnerTest() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        State argsState = State.WAITING;
        when(bookingRepository.findByItemOwnerIdAndStatusEquals(anyLong(), eq(WAITING), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));

        Collection<Booking> bookings = serviceImp.getAllByOwner(1L, argsState.name(), page, size, sortBy);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStatusEquals(anyLong(), any(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllBookingsByBookerTest() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        State argsState = State.ALL;
        when(bookingRepository.findByBookerId(anyLong(), any())).thenReturn(new PageImpl<>(List.of(new Booking())));

        Collection<Booking> bookings = serviceImp.getAllByBooker(2L, argsState.name(), page, size, sortBy);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findByBookerId(anyLong(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllPastBookingsByOwnerBookerTest() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        State argsState = State.PAST;
        long userId = booking.getItem().getOwner().getId();
        when(bookingRepository.findByBookerIdAndEndIsBefore(eq(userId), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));

        Collection<Booking> bookings = serviceImp.getAllByBooker(userId, argsState.name(), page, size, sortBy);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findByBookerIdAndEndIsBefore(anyLong(), any(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllCurrentBookingsByBookerTest() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        State argsState = State.CURRENT;
        when(bookingRepository.findCurrentByBookerId(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));

        Collection<Booking> bookings = serviceImp.getAllByBooker(2L, argsState.name(), page, size, sortBy);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findCurrentByBookerId(anyLong(), any(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllBookingsWithRejectedStateByBookerTest() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        State argsState = State.REJECTED;
        when(bookingRepository.findByBookerIdAndStatusEquals(anyLong(), eq(REJECTED), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));

        Collection<Booking> bookings = serviceImp.getAllByBooker(2L, argsState.name(), page, size, sortBy);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findByBookerIdAndStatusEquals(anyLong(), any(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllBookingsWithWaitingStateByBookerTest() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        State argsState = State.WAITING;
        when(bookingRepository.findByBookerIdAndStatusEquals(anyLong(), eq(WAITING), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));

        Collection<Booking> bookings = serviceImp.getAllByBooker(2L, argsState.name(), page, size, sortBy);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1)).findByBookerIdAndStatusEquals(anyLong(), any(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Check getAllBookingsByOwner throws StateValidationException when state is wrong")
    void getAllBookingsByOwnerFailTest() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        String argsState = "WRONG";
        final var thrown = assertThrows(
                StateValidationException.class, () -> serviceImp.getAllByOwner(1L, argsState, page, size, sortBy));
        assertEquals("Такого параметра не существует WRONG", thrown.getMessage());
    }

    @Test
    @DisplayName("Check getAllBookingsByBooker throws StateValidationException when state is wrong")
    void getAllBookingsByBookerFailTest() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        String argsState = "WRONG";
        final var thrown = assertThrows(
                StateValidationException.class, () -> serviceImp.getAllByBooker(2L, argsState, page, size, sortBy));
        assertEquals("Такого параметра не существует WRONG", thrown.getMessage());
    }

    @Test
    @DisplayName("Check getAllBookingsByOwner throws NotFoundException when user is wrong")
    void getAllBookingsByOwnerWrongUserTest() {
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImp.getAllByOwner(10L, State.ALL.name(), page, size, sortBy));
        assertEquals("Пользователь № 10 не найден", thrown.getMessage());
    }

    @Test
    @DisplayName("Check getAllBookingsByOwner throws NotFoundException when user is wrong")
    void getAllBookingsByBookerWrongUserTest() {
        final var thrown = assertThrows(
                NotFoundException.class, () -> serviceImp.getAllByBooker(10L, State.ALL.name(), page, size, sortBy));
        assertEquals("Пользователь № 10 не найден", thrown.getMessage());
    }
}