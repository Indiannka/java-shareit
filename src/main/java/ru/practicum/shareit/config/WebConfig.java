package ru.practicum.shareit.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.shareit.item.converter.ItemDtoToItemConverter;
import ru.practicum.shareit.item.converter.ItemToItemDtoConverter;
import ru.practicum.shareit.user.converter.UserDtoToUserConverter;
import ru.practicum.shareit.user.converter.UserToUserDtoConverter;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ItemDtoToItemConverter());
        registry.addConverter(new ItemToItemDtoConverter());

        registry.addConverter(new UserDtoToUserConverter());
        registry.addConverter(new UserToUserDtoConverter());
    }
}
