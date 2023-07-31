package ru.practicum.ewm.dto.request;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.request.ParticipationRequest;
@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }
}
