package ru.practicum.ewm.comments;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.event.EventMapper;

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
