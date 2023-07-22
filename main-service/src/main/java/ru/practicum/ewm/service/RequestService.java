package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Data
@AllArgsConstructor
public class RequestService {

    ParticipationRequestRepository repository;
    UserRepository userRepository;
    EventRepository eventRepository;

    ModelMapper mapper;
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        LocalDateTime now = LocalDateTime.now();
        User requester = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        ParticipationRequest request = ParticipationRequest.builder()
                .requester(requester)
                .created(now)
                .event(event)
                .state("")
                .build();
        repository.save(request);
        return mapper.map(repository.save(request),ParticipationRequestDto.class);
    }
}
