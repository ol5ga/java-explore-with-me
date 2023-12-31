package ru.practicum.ewm.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.client.stats.StatsClient;
import ru.practicum.dto.stats.EndpointHit;
import ru.practicum.ewm.config.AppName;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.location.LocationDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.request.RequestMapper;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.request.ParticipationState;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    private ViewService viewService;
    private StatsClient statsClient;
    private ModelMapper mapper;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AppName appClass;

    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        User initiator = userRepository.findById(userId).orElseThrow();
        List<Event> events = repository.findAllByInitiatorOrderById(initiator, PageRequest.of(from / size, size));
        Map<Long, Integer> views = viewService.getViews(events);
        addConfirmedRequest(events);
        return events.stream()
                .map(event -> EventMapper.toEventShortDto(event,
                        event.getConfirmedRequest(),
                        mapper.map(event.getCategory(), CategoryDto.class),
                        mapper.map(event.getInitiator(), UserShortDto.class), views))
                .collect(Collectors.toList());
    }

    public EventFullDto addEvent(long userId, NewEventDto request) {
        LocalDateTime now = LocalDateTime.now();
        if (request.getEventDate().isBefore(now.plusHours(2))) {
            throw new ConflictException("Событие не удовлетворяет правилам создания");
        }
        Category category = categoryRepository.findById(request.getCategory()).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        Location location = locationRepository.save(mapper.map(request.getLocation(), Location.class));

        Event event = repository.save(EventMapper.toEvent(request, category, now, user, location));
        Map<Long, Integer> views = viewService.getViews(List.of(event));
        Integer confirmedRequest = requestRepository.findAllByEventAndStatusOrderByCreated(event, ParticipationState.CONFIRMED).size();
        return collectToEventFullDto(event, confirmedRequest, views.get(event.getId()));
    }


    public EventFullDto getId(long userId, long eventId) {
        Event event = repository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено или недоступно"));
        if (event.getInitiator().getId() != userId) {
            throw new ValidationException("Запрос составлен некорректно");
        }
        Map<Long, Integer> views = viewService.getViews(List.of(event));
        Integer confirmedRequest = requestRepository.findAllByEventAndStatusOrderByCreated(event, ParticipationState.CONFIRMED).size();
        return collectToEventFullDto(event, confirmedRequest, views.get(eventId));
    }

    public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Event event = repository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено или недоступно"));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие не удовлетворяет правилам редактирования");
        }
        if (event.getInitiator().getId() != userId) {
            throw new ValidationException("Запрос составлен некорректно");
        }
        if (request.getAnnotation() != null && !request.getAnnotation().isBlank()) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            event.setCategory(mapper.map(request.getCategory(), Category.class));
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            if (request.getEventDate().isBefore(now.plusHours(2))) {
                throw new ValidationException("Событие не удовлетворяет правилам редактирования");
            }
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            Location location = locationRepository.save(mapper.map(request.getLocation(), Location.class));
            event.setLocation(location);
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(UserStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if (request.getStateAction().equals(UserStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            event.setTitle(request.getTitle());
        }
        Map<Long, Integer> views = viewService.getViews(List.of(event));
        Integer confirmedRequest = requestRepository.findAllByEventAndStatusOrderByCreated(event, ParticipationState.CONFIRMED).size();
        return collectToEventFullDto(repository.save(event), confirmedRequest, views.get(eventId));
    }

    public List<ParticipationRequestDto> getEventsRequests(long userId, long eventId) {
        Event event = repository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено или недоступно"));
        if (userId != event.getInitiator().getId()) {
            throw new ValidationException("Запрос составлен некорректно");
        }
        List<ParticipationRequest> requests = requestRepository.findAllByEvent(event);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult updateRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request) {
        if (request == null) {
            throw new ConflictException("Не передан список заявок");
        }
        Event event = repository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено или недоступно"));
        if (userId != event.getInitiator().getId()) {
            throw new ValidationException("Запрос составлен некорректно");
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ValidationException("Запрос составлен некорректно");
        }
        int pendingRequest = requestRepository.findAllByEventAndStatusOrderByCreated(event, ParticipationState.CONFIRMED).size();
        if (event.getParticipantLimit() <= pendingRequest) {
            throw new ConflictException("Достигнут лимит одобренных заявок");
        }
        List<ParticipationRequest> requests = requestRepository.findAllById(request.getRequestIds());
        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();
        if (request.getStatus().equals(ParticipationState.CONFIRMED)) {
            for (ParticipationRequest pR : requests) {
                if (!pR.getStatus().equals(ParticipationState.PENDING)) {
                    throw new ConflictException("Заявка должна быть в состоянии ожидания");
                }
                if (event.getParticipantLimit() > pendingRequest) {
                    pR.setStatus(ParticipationState.CONFIRMED);
                    confirmed.add(pR);
                } else {
                    pR.setStatus(ParticipationState.REJECTED);
                    rejected.add(pR);
                }
            }
        } else {
            for (ParticipationRequest pR : requests) {
                pR.setStatus(ParticipationState.REJECTED);
                rejected.add(pR);
            }
        }
        requestRepository.saveAll(confirmed);
        List<ParticipationRequestDto> confirmedRequests = confirmed.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
        List<ParticipationRequestDto> rejectedRequests = rejected.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }


    public List<EventFullDto> searchEvents(List<Long> usersId, List<EventState> states, List<Long> categoriesId, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        if (usersId != null) {
            List<User> users = usersId.stream()
                    .map(user -> userRepository.findById(user).orElseThrow())
                    .collect(Collectors.toList());
            conditions.add(event.initiator.in(users));
        }
        if (states != null) {
            conditions.add(event.state.in(states));
        }
        if (categoriesId != null) {
            List<Category> categories = categoryRepository.findByIdIn(categoriesId);
            if (categoriesId.size() == categories.size()) {
                conditions.add(event.category.in(categories));
            }
        }
        if (rangeStart != null) {
            conditions.add(event.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            conditions.add(event.eventDate.loe(rangeEnd));
        }
        List<Event> result = new ArrayList<>();
        if (conditions.isEmpty()) {
            result = repository.findAll(PageRequest.of(from / size, size)).getContent();
        } else {
            BooleanExpression request = conditions.get(0);
            for (int i = 1; i < conditions.size(); i++) {
                request = request.and(conditions.get(i));
            }
            Iterable<Event> events = repository.findAll(request, PageRequest.of(from / size, size));
            events.forEach(result::add);
        }
        Map<Long, Integer> views = viewService.getViews(result);
        addConfirmedRequest(result);
        return result.stream()
                .map(e -> collectToEventFullDto(e, e.getConfirmedRequest(), views.getOrDefault(e.getId(), 0)))
                .collect(Collectors.toList());
    }

    public EventFullDto adminUpdateEvent(long eventId, UpdateEventAdminRequest request) {
        Event event = repository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено или недоступно"));
        LocalDateTime now = LocalDateTime.now();
        if (request.getAnnotation() != null && !request.getAnnotation().isBlank()) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            event.setCategory(categoryRepository.findById(request.getCategory()).orElseThrow());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            if (request.getEventDate().isBefore(now.plusHours(1))) {
                throw new ValidationException("Событие не удовлетворяет правилам редактирования");
            }
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            Location location = locationRepository.save(mapper.map(request.getLocation(), Location.class));
            event.setLocation(location);
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(AdminStateAction.PUBLISH_EVENT) &&
                    (event.getState().equals(EventState.PUBLISHED) || (event.getState().equals(EventState.CANCELED)))) {
                throw new ConflictException("Событие не удовлетворяет правилам редактирования");
            }
            if (request.getStateAction().equals(AdminStateAction.REJECT_EVENT)
                    && event.getState().equals(EventState.PUBLISHED)) {
                throw new ConflictException("Событие не удовлетворяет правилам редактирования");
            }
            if (request.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                event.setPublishedOn(now);
                event.setState(EventState.PUBLISHED);
            } else if (request.getStateAction().equals(AdminStateAction.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            event.setTitle(request.getTitle());
        }
        Map<Long, Integer> views = viewService.getViews(List.of(event));
        Integer confirmedRequest = requestRepository.findAllByEventAndStatusOrderByCreated(event, ParticipationState.CONFIRMED).size();
        return collectToEventFullDto(repository.save(event), confirmedRequest, views.get(eventId));
    }

    public EventFullDto getEvent(long eventId, HttpServletRequest httpRequest) {
        Event event = repository.findById(eventId).orElseThrow(() -> new StorageException("Событие не найдено или недоступно"));
        if (event.getState().equals(EventState.PENDING) || event.getState().equals(EventState.CANCELED)) {
            throw new StorageException("Запрос составлен некорректно");
        }
        statsClient.saveStats(new EndpointHit(appClass.getAppName(), httpRequest.getRequestURI(), httpRequest.getRemoteAddr(), LocalDateTime.now().format(formatter)));
        Map<Long, Integer> views = viewService.getViews(List.of(event));
        Integer confirmedRequest = requestRepository.findAllByEventAndStatusOrderByCreated(event, ParticipationState.CONFIRMED).size();
        return collectToEventFullDto(event, confirmedRequest, views.get(eventId));
    }


    public List<EventShortDto> getEvents(String text, List<Long> categoriesId, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from, int size, HttpServletRequest httpRequest) {
        LocalDateTime now = LocalDateTime.now();
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = now;
            rangeEnd = rangeStart.plusYears(1000);
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата начала сортировки должна быть ранне конца сортировки");
        }
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        if (text != null) {
            conditions.add(event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text)));
        }
        if (categoriesId != null) {
            List<Category> categories = categoryRepository.findByIdIn(categoriesId);
            if (categoriesId.size() == categories.size()) {
                conditions.add(event.category.in(categories));
            }
        }
        if (paid != null) {
            conditions.add(event.paid.eq(paid));
        }
        if (rangeStart != now) {
            conditions.add(event.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            conditions.add(event.eventDate.loe(rangeEnd));
        }
        List<Event> result = new ArrayList<>();
        BooleanExpression request = event.state.eq(EventState.PUBLISHED);
        if (!conditions.isEmpty()) {
            for (BooleanExpression condition : conditions) {
                request = request.and(condition);
            }
        }
        Pageable page = PageRequest.of(from / size, size);
        if (sort != null && sort.equals("EVENT_DATE")) {
            page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "eventDate"));
        }
        Iterable<Event> events = repository.findAll(request, page);
        events.forEach(result::add);
        statsClient.saveStats(new EndpointHit(appClass.getAppName(), httpRequest.getRequestURI(), httpRequest.getRemoteAddr(), now.format(formatter)));
        Map<Long, Integer> views = viewService.getViews(result);

        if (onlyAvailable != null && onlyAvailable) {
            addConfirmedRequest(result);
            result = result.stream()
                    .filter(e -> e.getConfirmedRequest() < e.getParticipantLimit())
                    .collect(Collectors.toList());
        }
        List<EventShortDto> resultDto = result.stream()
                .map(e -> EventMapper.toEventShortDto(e,
                        e.getConfirmedRequest(),
                        mapper.map(e.getCategory(), CategoryDto.class),
                        mapper.map(e.getInitiator(), UserShortDto.class), views))
                .collect(Collectors.toList());
        if (sort != null && sort.equals("VIEWS")) {
            resultDto.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .collect(Collectors.toList());
        }
        return resultDto;
    }

    private void addConfirmedRequest(List<Event> result) {
        List<ParticipationRequest> allRequest = requestRepository.findAllByEventIn(result);
        for (ParticipationRequest pR : allRequest) {
            if (pR.getStatus().equals(ParticipationState.CONFIRMED)) {
                Event event1 = pR.getEvent();
                event1.setConfirmedRequest(event1.getConfirmedRequest() + 1);
            }
        }
    }

    private EventFullDto collectToEventFullDto(Event event, Integer confirmedRequests, Integer views) {
        CategoryDto categoryDto = mapper.map(event.getCategory(), CategoryDto.class);
        UserShortDto userDto = mapper.map(event.getInitiator(), UserShortDto.class);
        LocationDto locationDto = mapper.map(event.getLocation(), LocationDto.class);
        return EventMapper.toEventFullDto(event, confirmedRequests, categoryDto, userDto, locationDto, views);
    }

}
