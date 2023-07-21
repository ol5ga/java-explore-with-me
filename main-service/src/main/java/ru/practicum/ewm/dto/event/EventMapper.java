package ru.practicum.ewm.dto.event;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.Event;


public class EventMapper {
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
