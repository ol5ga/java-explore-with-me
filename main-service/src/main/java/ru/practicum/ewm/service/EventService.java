package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.location.LocationDto;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.category.CategoryRepository;
import ru.practicum.ewm.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@AllArgsConstructor
public class EventService {

    private EventRepository repository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private LocationRepository locationRepository;
    private ParticipationRequestRepository requestRepository;
    private ModelMapper mapper;

    public List<EventShortDto> addUserEvents(long userId, int from, int size) {
        User initiator = userRepository.findById(userId).orElseThrow();
        List<Event> events = repository.findAllByInitiatorOrderById(initiator, PageRequest.of(from/size, size));
        return events.stream()
                .map(event -> EventMapper.toEventShortDto(event,
                        requestRepository.findAllByEventAndState(event,"APPROVED").size(),
                        mapper.map(event.getCategory(), CategoryDto.class),
                        mapper.map(event.getInitiator(),UserShortDto.class)))
                .collect(Collectors.toList());
    }

    public EventFullDto addEvent(long userId, NewEventDto request) {
        LocalDateTime now = LocalDateTime.now();
        if(request.getEventDate().isBefore(now.plusHours(2))){
            throw new ConflictException("Событие не удовлетворяет правилам создания");
        }
        Category category = categoryRepository.findById(request.getCategory()).orElseThrow();
        if(request.getRequestModeration() == null){
            request.setRequestModeration(true);
        }
        User user = userRepository.findById(userId).orElseThrow();
        Location location = locationRepository.save(mapper.map(request.getLocation(),Location.class));

        Event event = repository.save(EventMapper.toEvent(request, category, now, user, location));

        return collectToEventFullDto(event);
    }


    public EventFullDto getId(long userId, long eventId) {
        Event event = repository.findById(eventId).orElseThrow(()-> new StorageException("Событие не найдено или недоступно"));
        if (event.getInitiator().getId() != userId ){
            throw new ValidationException("Запрос составлен некорректно");
        }
        return collectToEventFullDto(event);
    }

    public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Event event = repository.findById(eventId).orElseThrow(()-> new StorageException("Событие не найдено или недоступно"));
        if(!event.getState().equals("PUBLISHED")|| request.getEventDate().isBefore(now.plusHours(2))){
            throw new ConflictException("Событие не удовлетворяет правилам редактирования");
        }
        if (event.getInitiator().getId() != userId ){
            throw new ValidationException("Запрос составлен некорректно");
        }
        if(request.getAnnotation() !=null){
            event.setAnnotation(request.getAnnotation());
        } else if (request.getCategory() != null){
            event.setCategory(mapper.map(request.getCategory(), Category.class));
        } else if(request.getDescription() != null){
            event.setDescription(request.getDescription());
        } else if(request.getEventDate() != null){
            event.setEventDate(request.getEventDate());
        } else if (request.getLocation() != null){
            event.setLocation(mapper.map(request.getLocation(), Location.class));
        } else if(request.getPaid() != null){
            event.setPaid(request.getPaid());
        } else if(request.getParticipantLimit() != null){
            event.setParticipantLimit(request.getParticipantLimit());
        } else if(request.getRequestModeration() != event.getRequestModeration()){
            event.setRequestModeration(request.getRequestModeration());
        } else if(request.getStateAction() != null){
            if(request.getStateAction().equals("SEND_TO_REVIEW")) {
                event.setState("PENDING");
            } else if(request.getStateAction().equals("CANCEL_REVIEW")){
                event.setState("CANCELED");
            }
        } else if(request.getTitle() != null){
            event.setTitle(request.getTitle());
        }
        repository.save(event);
        return collectToEventFullDto(repository.save(event));
    }

    private EventFullDto collectToEventFullDto(Event event){
        Integer confirmedRequests = requestRepository.findAllByEventAndState(event,"APPROVED").size();
        CategoryDto categoryDto = mapper.map(event.getCategory(), CategoryDto.class);
        UserShortDto userDto = mapper.map(event.getInitiator(),UserShortDto.class);
        LocationDto locationDto = mapper.map(event.getLocation(), LocationDto.class);
        return EventMapper.toEventFullDto(event,confirmedRequests,categoryDto,userDto,locationDto);
    }
}
