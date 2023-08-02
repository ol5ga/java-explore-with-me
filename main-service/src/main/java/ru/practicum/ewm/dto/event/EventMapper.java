package ru.practicum.ewm.dto.event;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.location.LocationDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.user.User;

import java.time.LocalDateTime;
import java.util.Map;

@UtilityClass
public class EventMapper {
    public Event toEvent(NewEventDto request, Category category, LocalDateTime now, User user, Location location) {
        return Event.builder()
                .annotation(request.getAnnotation())
                .category(category)
                .createdOn(now)
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .initiator(user)
                .location(location)
                .paid(request.getPaid())
                .participantLimit(request.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(request.getRequestModeration())
                .state(EventState.PENDING)
                .title(request.getTitle())
                .views(0)
                .build();
    }

    public EventFullDto toEventFullDto(Event event, Integer confirmedRequests, CategoryDto categoryDto, UserShortDto userDto, LocationDto locationDto, Integer views) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(userDto)
                .location(locationDto)
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public EventShortDto toEventShortDto(Event event, Integer confirmedRequests, CategoryDto categoryDto, UserShortDto userDto, Map<Long, Integer> views) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(userDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views.get(event.getId()))
                .build();
    }
}
