package ru.practicum.ewm.comments;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    public List<CommentAdminDto> getList(LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        return new ArrayList<>();
    }

    public CommentAdminDto updateStates(Long commentId) {
        return new CommentAdminDto();
    }


    public CommentDto addComment(long userId, NewCommentDto newComment) {
        return new CommentDto();
    }

    public List<CommentDto> getComments(long userId, long eventId) {
        return new ArrayList<>();
    }

    public void deleteComment(long userId, long eventId) {
    }

    public CommentDto updateComment(long userId, long commentId, NewCommentDto newComment) {
       return new CommentDto();
    }
}
