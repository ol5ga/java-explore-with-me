package ru.practicum.ewm.comments;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findFirstByEventAndAuthor(Event event, User author);
}
