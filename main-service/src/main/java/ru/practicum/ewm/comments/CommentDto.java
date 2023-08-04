package ru.practicum.ewm.comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.user.UserShortDto;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private EventShortDto event;
    private String text;
    private String authorName;
    private LocalDateTime published;
}
