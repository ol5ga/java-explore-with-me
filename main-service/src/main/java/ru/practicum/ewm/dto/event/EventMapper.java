package ru.practicum.ewm.dto.event;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class EventMapper {
    public static Event toEvent(NewEventDto request, Category category, LocalDateTime now, User user, Location location){
        return Event.builder()
                .annotation(request.getAnnotation())
                .category(category)
                .requests(new ArrayList<>())
                .createdOn(now)
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .initiator(user)
                .location(location)
                .paid(request.isPaid())
                .participantLimit(request.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(request.getRequestModeration())
                .state("PENDING")
                .title(request.getTitle())
                .views(0)
                .build();
    }
    public static Event toEvent(NewEventDto request, Category category, List<ParticipationRequest> requests, LocalDateTime now, User user, Location location){
        return Event.builder()
                .annotation(request.getAnnotation())
                .category(category)
                .requests(requests)
                .createdOn(now)
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .initiator(user)
                .location(location)
                .paid(request.isPaid())
                .participantLimit(request.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(request.getRequestModeration())
                .state("PENDING")
                .title(request.getTitle())
                .views(0)
                .build();
    }
    public static EventFullDto toEventFullDto(Event event, Integer confirmedRequests,CategoryDto categoryDto,UserShortDto userDto,LocationDto locationDto){
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
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
