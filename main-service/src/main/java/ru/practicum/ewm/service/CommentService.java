package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.comments.CommentDto;
import ru.practicum.ewm.dto.comments.CommentMapper;
import ru.practicum.ewm.dto.comments.NewCommentDto;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.model.comment.Comment;
import ru.practicum.ewm.model.comment.CommentState;
import ru.practicum.ewm.model.comment.StateAction;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    CommentRepository repository;

    UserRepository userRepository;

    EventRepository eventRepository;

    public List<CommentDto> getList(LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now();
        }
        if (rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Неверно указан временной интервал");
        }
        Pageable page = PageRequest.of(from / size, size);
        List<Comment> comments = repository.findAllByCreatedIsAfterAndCreatedIsBeforeOrderByCreated(rangeStart, rangeEnd, page);
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public CommentDto updateStates(Long commentId, StateAction action) {
        Comment comment = repository.findById(commentId).orElseThrow(() -> new StorageException("Комментарий не найден"));
        if (action == StateAction.PUBLISH) {
            comment.setState(CommentState.PUBLISHED);
        } else if (action == StateAction.REJECT) {
            comment.setState(CommentState.CANCELED);
        }
        repository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }


    public CommentDto addComment(long userId, long eventId, NewCommentDto newComment) {
        LocalDateTime now = LocalDateTime.now();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено"));
        User author = userRepository.findById(userId).orElseThrow(() -> new StorageException("Пользователь не найден"));
        if (repository.existsByAuthor(author)) {
            throw new ConflictException("Нельзя оставить комментарий повторно");
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
        List<Comment> comments = repository.findAllByEventAndState(event, CommentState.PUBLISHED);
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public void deleteComment(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено"));
        User author = userRepository.findById(userId).orElseThrow(() -> new StorageException("Пользователь не найден"));
        Comment comment = repository.findFirstByEventAndAuthor(event, author);
        if (author.equals(comment.getAuthor())) {
            repository.delete(comment);
        }
    }

    public CommentDto updateComment(long userId, long commentId, NewCommentDto newComment) {
        User author = userRepository.findById(userId).orElseThrow(() -> new StorageException("Пользователь не найден"));
        Comment comment = repository.findById(commentId).orElseThrow(() -> new StorageException("Коментарий не найден"));
        if (author != comment.getAuthor()) {
            throw new ValidationException("Комментарий может изменить только автор");
        }
        comment.setText(newComment.getText());
        return CommentMapper.toCommentDto(repository.save(comment));
    }
}
