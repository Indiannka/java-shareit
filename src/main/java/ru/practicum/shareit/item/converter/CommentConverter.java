package ru.practicum.shareit.item.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.dto.CommentDto;

@Component
@AllArgsConstructor
public class CommentConverter {
    public CommentDto convert(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment convert(CommentDto commentDTO) {
        return Comment.builder()
                .text(commentDTO.getText())
                .build();
    }
}
