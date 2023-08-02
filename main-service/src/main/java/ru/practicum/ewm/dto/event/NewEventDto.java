package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import ru.practicum.ewm.dto.location.LocationDto;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    private Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @Future
    private LocalDateTime eventDate;
    @NotNull
    @Valid
    private LocationDto location;
    @Nullable
    private Boolean paid;
    @Nullable
    @PositiveOrZero
    private Integer participantLimit;
    @Nullable
    private Boolean requestModeration;
    @NotNull
    @Size(min = 3, max = 120)
    private String title;
}
