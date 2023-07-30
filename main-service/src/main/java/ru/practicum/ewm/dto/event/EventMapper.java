package ru.practicum.ewm.dto.event;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.location.LocationDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.user.User;

import java.time.LocalDateTime;


public class EventMapper {
    public static Event toEvent(NewEventDto request, Category category, LocalDateTime now, User user, Location location) {
        return Event.builder()
                .annotation(request.getAnnotation())
                .category(category)
                .confirmedRequests(0)
                .createdOn(now)
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .initiator(user)
                .location(location)
                .paid(request.getPaid())
                .participantLimit(request.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(request.getRequestModeration())
                .state("PENDING")
                .title(request.getTitle())
                .views(0)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event, CategoryDto categoryDto, UserShortDto userDto, LocationDto locationDto) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event.getConfirmedRequests())
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
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event, CategoryDto categoryDto, UserShortDto userDto) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(userDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
