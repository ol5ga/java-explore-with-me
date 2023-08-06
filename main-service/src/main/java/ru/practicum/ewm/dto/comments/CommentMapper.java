package ru.practicum.ewm.dto.comments;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.comments.CommentDto;
import ru.practicum.ewm.model.Comment;

@UtilityClass
public class CommentMapper {

    public CommentDto toCommentDto (Comment comment){
        return CommentDto.builder()
                .event(comment.getEvent().getAnnotation())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated()).build();
    }
}
