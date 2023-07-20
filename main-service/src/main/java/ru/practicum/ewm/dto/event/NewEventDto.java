package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.practicum.ewm.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 200)
    private String annotation;
    @NotNull
    private int category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @Future
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    @Nullable
    private boolean paid;
    @Nullable
    private Integer participantLimit;

    @NotNull
    @Size(min = 3, max = 120)
    private String title;
}
