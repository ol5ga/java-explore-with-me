package ru.practicum.ewm.comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.user.UserShortDto;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentAdminDto {
    private Long id;
    private String text;

    private EventShortDto event;

    private UserShortDto author;

    private LocalDateTime created;

    private CommentState state;
}
