package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.location.LocationDto;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.request.RequestMapper;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@AllArgsConstructor
@Slf4j
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
                        requestRepository.findAllByEventAndStateOrderByCreated(event,"APPROVED").size(),
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
        if(!(event.getState().equals("PENDING") || event.getState().equals("CANCELED"))){
            throw new ConflictException("Событие не удовлетворяет правилам редактирования");
        }
        if (event.getInitiator().getId() != userId ){
            throw new ValidationException("Запрос составлен некорректно");
        }

        if(request.getAnnotation() !=null){
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null){
            event.setCategory(mapper.map(request.getCategory(), Category.class));
        }
        if(request.getDescription() != null){
            event.setDescription(request.getDescription());
        }
        if(request.getEventDate() != null){
            if(request.getEventDate().isBefore(now.plusHours(2))){
                throw new ConflictException("Событие не удовлетворяет правилам редактирования");
            }
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null){
            Location location = locationRepository.save(mapper.map(request.getLocation(), Location.class));
            event.setLocation(location);
        }
        if(request.getPaid() != null){
            event.setPaid(request.getPaid());
        }
        if(request.getParticipantLimit() != null){
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if(request.getRequestModeration() != event.getRequestModeration()){
            event.setRequestModeration(request.getRequestModeration());
        }
        if(request.getStateAction() != null){
            if(request.getStateAction().equals("SEND_TO_REVIEW")) {
                event.setState("PENDING");
            } else if(request.getStateAction().equals("CANCEL_REVIEW")){
                event.setState("CANCELED");
            }
        }
        if(request.getTitle() != null){
            event.setTitle(request.getTitle());
        }
        repository.save(event);
        return collectToEventFullDto(repository.save(event));
    }

    public List<ParticipationRequestDto> getEventsRequests(long userId, long eventId) {
        Event event = repository.findById(eventId).orElseThrow(()->new StorageException("Событие не найдено или недоступно"));
        if(userId != event.getInitiator().getId()){
            throw new ValidationException("Запрос составлен некорректно");
        }
        List<ParticipationRequest> requests = requestRepository.findAllByEvent(event);
        List<ParticipationRequestDto> response = new ArrayList<>();
        response = requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        return response;
    }

    public EventRequestStatusUpdateResult updateRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request) {
        Event event = repository.findById(eventId).orElseThrow(()->new StorageException("Событие не найдено или недоступно"));
        if(userId != event.getInitiator().getId()){
            throw new ValidationException("Запрос составлен некорректно");
        }
        if(!event.getRequestModeration() || event.getParticipantLimit() == 0){
            throw new ValidationException("Запрос составлен некорректно");
        }
        int pendingRequest = requestRepository.findAllByEventAndStateOrderByCreated(event,"PENDING").size();
        if(event.getParticipantLimit() == pendingRequest){
            throw new ConflictException("Достигнут лимит одобренных заявок");
        }
        List<ParticipationRequest> requests = requestRepository.findAllById(request.getRequestIds());
        if(request.getStatus().equals("CONFIRMED")) {
            for (ParticipationRequest pR : requests) {
                if (!pR.getState().equals("PENDING")) {
                    throw new ConflictException("Заявка должна быть в состоянии ожидания");
                }
                if (event.getParticipantLimit() != pendingRequest) {
                    pendingRequest++;
                    pR.setState("CONFIRMED");
                } else{
                    pR.setState("REJECTED");
                }
            }
        } else {
            for (ParticipationRequest pR : requests) {
                pR.setState("REJECTED");
            }
        }
        List<ParticipationRequest> confirmed = requestRepository.findAllByEventAndStateOrderByCreated(event,"CONFIRMED");
        List<ParticipationRequest> rejected = requestRepository.findAllByEventAndStateOrderByCreated(event,"REJECTED");
        List<ParticipationRequestDto> confirmedRequests = confirmed.stream().map(ex -> RequestMapper.toParticipationRequestDto(ex)).collect(Collectors.toList());
        List<ParticipationRequestDto> rejectedRequests = rejected.stream().map(ex -> RequestMapper.toParticipationRequestDto(ex)).collect(Collectors.toList());
    return EventRequestStatusUpdateResult.builder()
            .confirmedRequests(confirmedRequests)
            .rejectedRequests(rejectedRequests)
            .build();
    }



    public List<EventFullDto> searchEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        //TODO

        return new ArrayList<>();
    }

    public EventFullDto adminUpdateEvent(long eventId, UpdateEventAdminRequest request) {
        Event event = repository.findById(eventId).orElseThrow(()->new StorageException("Событие не найдено или недоступно"));
        LocalDateTime now = LocalDateTime.now();
        if(request.getStateAction().equals("PUBLISH_EVENT") && !event.getState().equals("PENDING")){
            throw new ConflictException("Событие не удовлетворяет правилам редактирования");
        }
        if(request.getStateAction().equals("REJECT_EVENT") && event.getState().equals("PUBLISHED")){
            throw new ConflictException("Событие не удовлетворяет правилам редактирования");
        }

        if(request.getAnnotation() !=null){
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null){
            event.setCategory(categoryRepository.findById(request.getCategory()).orElseThrow());
        }
        if(request.getDescription() != null){
            event.setDescription(request.getDescription());
        }
        if(request.getEventDate() != null){
            if(request.getEventDate().isBefore(now.plusHours(1))){
                throw new ConflictException("Событие не удовлетворяет правилам редактирования");
            }
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null){
            Location location = locationRepository.save(mapper.map(request.getLocation(), Location.class));
            event.setLocation(location);
        }
        if(request.getPaid() != null){
            event.setPaid(request.getPaid());
        }
        if(request.getParticipantLimit() != null){
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if(request.getRequestModeration() != event.getRequestModeration()){
            event.setRequestModeration(request.getRequestModeration());
        }
        if(request.getStateAction() != null){
            if(request.getStateAction().equals("PUBLISH_EVENT")) {
                event.setState("PUBLISHED");
            } else if(request.getStateAction().equals("REJECT_EVENT")){
                event.setState("REJECTED");
            }
        }
        if(request.getTitle() != null){
            event.setTitle(request.getTitle());
        }
        repository.save(event);
        return collectToEventFullDto(repository.save(event));
    }
    public EventFullDto getEvent(long eventId) {
        Event event = repository.findById(eventId).orElseThrow(()-> new StorageException("Событие не найдено или недоступно"));
        if(!event.getState().equals("PUBLISHED")){
            throw new ValidationException("Запрос составлен некорректно");
        }
        //TODO добавить в статистику и взять из нее
        return collectToEventFullDto(event);
    }

    private EventFullDto collectToEventFullDto(Event event){
        Integer confirmedRequests = requestRepository.findAllByEventAndStateOrderByCreated(event,"APPROVED").size();
        CategoryDto categoryDto = mapper.map(event.getCategory(), CategoryDto.class);
        UserShortDto userDto = mapper.map(event.getInitiator(),UserShortDto.class);
        LocationDto locationDto = mapper.map(event.getLocation(), LocationDto.class);
        return EventMapper.toEventFullDto(event,confirmedRequests,categoryDto,userDto,locationDto);
    }
}
