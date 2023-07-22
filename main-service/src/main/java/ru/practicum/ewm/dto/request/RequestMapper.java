package ru.practicum.ewm.dto.request;

import ru.practicum.ewm.model.request.ParticipationRequest;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request){
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .state(request.getState())
                .build();
    }
}
