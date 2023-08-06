package ru.practicum.ewm.dto.comments;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.event.AdminStateAction;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentAdminDto {
    private Long id;
    private String text;

    private EventShortDto event;

    private UserShortDto author;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    private AdminStateAction stateAction;
}
