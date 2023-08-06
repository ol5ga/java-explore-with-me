package ru.practicum.ewm.comments;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.model.event.AdminStateAction;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    CommentRepository repository;

    UserRepository userRepository;

    EventRepository eventRepository;
    public List<CommentAdminDto> getList(LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        return new ArrayList<>();
    }

    public CommentDto updateStates(Long commentId, AdminStateAction action) {
        Comment comment = repository.findById(commentId).orElseThrow(() -> new StorageException("Коментарий не найден"));
        if(action == AdminStateAction.PUBLISH){
            comment.setState(CommentState.PUBLISHED);
        } else if(action == AdminStateAction.REJECT){
            comment.setState(CommentState.CANCELED);
        }
        repository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }


    public CommentDto addComment(long userId,long eventId, NewCommentDto newComment) {
        LocalDateTime now = LocalDateTime.now();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено"));
        User author = userRepository.findById(userId).orElseThrow(() -> new StorageException("Пользователь не найден"));
        if(repository.existsByAuthor(author)){
            throw new ConflictException("Нельзя оставить комментрарий повторно");
        }
        Comment comment = Comment.builder()
                .text(newComment.getText())
                .event(event)
                .author(author)
                .created(now)
                .state(CommentState.PENDING)
                .build();
        return CommentMapper.toCommentDto(repository.save(comment));
    }

    public List<CommentDto> getComments(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено"));
        List<Comment> comments = repository.findAllByEvent(event);
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public void deleteComment(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено"));
        User author = userRepository.findById(userId).orElseThrow(() -> new StorageException("Пользователь не найден"));
        Comment comment = repository.findFirstByEventAndAuthor(event,author);
        if(author.equals(comment.getAuthor())){
            repository.delete(comment);
        }
    }

    public CommentDto updateComment(long userId, long commentId, NewCommentDto newComment) {
       return new CommentDto();
    }
}