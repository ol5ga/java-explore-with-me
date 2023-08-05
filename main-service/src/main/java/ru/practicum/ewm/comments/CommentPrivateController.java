package ru.practicum.ewm.comments;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class CommentPrivateController {

    CommentService service;

    @PostMapping(path = "/{userId}/comment/{eventId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable long userId, @PathVariable long eventId, @Valid @RequestBody NewCommentDto newComment){
        return service.addComment(userId,eventId, newComment);
    }

    @GetMapping(path = "/{userId}/comment/{eventId}")
    public List<CommentDto> getComments(@PathVariable long userId, @PathVariable long eventId){
        return service.getComments(userId,eventId);
    }

    @DeleteMapping(path = "/{userId}/comment/{eventId}")
    public void deleteComment(@PathVariable long userId, @PathVariable long eventId){
        service.deleteComment(userId, eventId);
    }

    @PatchMapping(path = "/{userId}/comment/{commentId}")
    public CommentDto update(@PathVariable long userId, @PathVariable long commentId, @RequestBody NewCommentDto newComment){
        return service.updateComment(userId, commentId,newComment);
    }
}
