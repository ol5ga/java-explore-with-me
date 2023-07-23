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
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Data
@AllArgsConstructor
@Slf4j
public class RequestService {

    ParticipationRequestRepository repository;
    UserRepository userRepository;
    EventRepository eventRepository;

    ModelMapper mapper;
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        LocalDateTime now = LocalDateTime.now();
        User requester = userRepository.findById(userId).orElseThrow(()-> new StorageException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new StorageException("Событие не найдено или недоступно"));
        if(userId == event.getInitiator().getId()||
                repository.findFirstByEvent_IdAndRequester_Id(eventId,userId) != null){
            log.info("Для этого пользователя нельзя создать запрос");
            throw new ConflictException("Нарушение целостности данных");
        }
        if (!event.getState().equals("PUBLISHED")) {
            log.info("Событие не опубликовано");
            throw new ConflictException("Нарушение целостности данных");
        }
        String state;
        if(event.getParticipantLimit() == 0){
            state = "CONFIRMED";
        }else if(repository.findAllByEventAndStateOrderByCreated(event,"CONFIRMED").size() == event.getParticipantLimit()){
            log.info("Достигнут лимит участников");
            throw new ConflictException("Нарушение целостности данных");
        } else{
            state = "PENDING";
        }
        //TODO why null??
        event.setRequestModeration(true);
        ParticipationRequest request = ParticipationRequest.builder()
                .requester(requester)
                .created(now)
                .event(event)
                .state(state)
                .build();

        return RequestMapper.toParticipationRequestDto(repository.save(request));
    }
}
