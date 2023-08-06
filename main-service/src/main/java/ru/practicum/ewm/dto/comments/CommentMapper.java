package ru.practicum.ewm.dto.comments;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.comment.Comment;

@UtilityClass
public class CommentMapper {

    public CommentDto toCommentDto (Comment comment){
        return CommentDto.builder()
                .id(comment.getId())
                .event(comment.getEvent().getAnnotation())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated()).build();
    }
}
