package ru.practicum.shareit.item.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.dto.CommentDTO;

public class CommentDtoToCommentConverter implements Converter<CommentDTO, Comment> {

    @Override
    public Comment convert(CommentDTO commentDTO) {
        return Comment.builder()
                .text(commentDTO.getText())
                .build();
    }
}
