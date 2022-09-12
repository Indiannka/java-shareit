package ru.practicum.shareit.item.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.dto.CommentDTO;

public class CommentToCommentDtoConverter implements Converter<Comment, CommentDTO> {

    @Override
    public CommentDTO convert(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
