package ru.practicum.shareit.item.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.dto.CommentDto;

@Component
@AllArgsConstructor
public class CommentDtoToCommentConverter {

    public Comment convert(CommentDto commentDTO) {
        return Comment.builder()
                .text(commentDTO.getText())
                .build();
    }
}
