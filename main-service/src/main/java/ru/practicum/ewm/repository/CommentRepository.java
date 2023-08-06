package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findFirstByEventAndAuthor(Event event, User author);

    List<Comment> findAllByEvent(Event event);

    Boolean existsByAuthor(User author);

    List<Comment> findAllCreatedIsAfterAndCreatedBeforeOrderByCreated(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
