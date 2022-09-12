package ru.practicum.shareit.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.shareit.booking.converter.BookingToBookingDto;
import ru.practicum.shareit.booking.converter.BookingToIncomingBookingDto;
import ru.practicum.shareit.booking.converter.IncomingBookingDTOToBooking;
import ru.practicum.shareit.item.converter.*;
import ru.practicum.shareit.user.converter.UserDtoToUserConverter;
import ru.practicum.shareit.user.converter.UserToUserDtoConverter;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ItemDtoToItemConverter());
        registry.addConverter(new ItemToItemDtoConverter());
        registry.addConverter(new ItemToItemWithBookingsConverter());

        registry.addConverter(new CommentDtoToCommentConverter());
        registry.addConverter(new CommentToCommentDtoConverter());


        registry.addConverter(new UserDtoToUserConverter());
        registry.addConverter(new UserToUserDtoConverter());

        registry.addConverter(new IncomingBookingDTOToBooking());
        registry.addConverter(new BookingToIncomingBookingDto());
        registry.addConverter(new BookingToBookingDto());
    }
}
