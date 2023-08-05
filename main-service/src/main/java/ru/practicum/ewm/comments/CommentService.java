package ru.practicum.ewm.comments;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    CommentRepository repository;

    UserRepository userRepository;

    EventRepository eventRepository;
    public List<CommentAdminDto> getList(LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        return new ArrayList<>();
    }

    public CommentAdminDto updateStates(Long commentId) {
        return new CommentAdminDto();
    }


    public CommentDto addComment(long userId,long eventId, NewCommentDto newComment) {
        LocalDateTime now = LocalDateTime.now();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено"));
        User author = userRepository.findById(userId).orElseThrow(() -> new StorageException("Событие не найдено"));
//TODO проверка, что пользователь еще не оставлял коментарий
        Comment comment = Comment.builder()
                .text(newComment.getText())
                .event(event)
                .author(author)
                .created(now)
                .state(CommentState.PENDING)
                .build();
        repository.save(comment);
        return new CommentDto();
    }

    public List<CommentDto> getComments(long userId, long eventId) {
        return new ArrayList<>();
    }

    public void deleteComment(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено"));
        User author = userRepository.findById(userId).orElseThrow(() -> new StorageException("Событие не найдено"));
        Comment comment = repository.findFirstByEventAndAuthor(event,author);
        if(author.equals(comment.getAuthor())){
            repository.delete(comment);
        }
    }

    public CommentDto updateComment(long userId, long commentId, NewCommentDto newComment) {
       return new CommentDto();
    }
}
