package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.comment.Comment;
import ru.practicum.ewm.model.comment.CommentState;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findFirstByEventAndAuthor(Event event, User author);

    List<Comment> findAllByEventAndState(Event event, CommentState state);

    Boolean existsByAuthor(User author);

    List<Comment> findAllByCreatedIsAfterAndCreatedIsBeforeOrderByCreated(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
