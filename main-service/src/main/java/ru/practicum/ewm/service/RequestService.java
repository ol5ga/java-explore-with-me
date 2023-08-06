package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.request.RequestMapper;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.request.ParticipationState;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@AllArgsConstructor
@Slf4j
public class RequestService {

    private ParticipationRequestRepository repository;
    private UserRepository userRepository;
    private EventRepository eventRepository;

    private ModelMapper mapper;

    public List<ParticipationRequestDto> getUsersRequests(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new StorageException("Пользователь не найден"));
        List<ParticipationRequest> requests = repository.findAllByRequester_Id(userId);
        return requests.stream()
                .map(request -> RequestMapper.toParticipationRequestDto(request))
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto addRequest(long userId, long eventId) {
        LocalDateTime now = LocalDateTime.now();
        User requester = userRepository.findById(userId).orElseThrow(() -> new StorageException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено или недоступно"));
        Integer confirmedRequests = repository.findAllByEvent(event).size();
        if (userId == event.getInitiator().getId() ||
                repository.findFirstByEvent_IdAndRequester_Id(eventId, userId) != null) {
            log.info("Для этого пользователя нельзя создать запрос");
            throw new ConflictException("Нарушение целостности данных");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.info("Событие не опубликовано");
            throw new ConflictException("Нарушение целостности данных");
        }
        if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
            log.info("Достигнут лимит участников");
            throw new ConflictException("Нарушение целостности данных");
        }
        ParticipationState state;
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            state = ParticipationState.CONFIRMED;
        } else {
            state = ParticipationState.PENDING;
        }
        ParticipationRequest request = ParticipationRequest.builder()
                .requester(requester)
                .created(now)
                .event(event)
                .status(state)
                .build();
        return RequestMapper.toParticipationRequestDto(repository.save(request));
    }


    public ParticipationRequestDto canceledRequest(long userId, long requestId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new StorageException("Пользователь не найден"));
        ParticipationRequest request = repository.findById(requestId).orElseThrow(() -> new StorageException("Запрос не найден или недоступен"));

        request.setStatus(ParticipationState.CANCELED);
        repository.delete(request);
        return RequestMapper.toParticipationRequestDto(request);
    }
}
