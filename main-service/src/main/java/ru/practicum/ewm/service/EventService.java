package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventMapper;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.category.CategoryRepository;
import ru.practicum.ewm.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public EventFullDto addEvent(long userId, NewEventDto request) {
        LocalDateTime now = LocalDateTime.now();
        Category category = categoryRepository.findById(request.getCategory()).orElseThrow();
        if(request.getRequestModeration() == null){
            request.setRequestModeration(true);
        }
        Location location = locationRepository.save(mapper.map(request.getLocation(),Location.class));
        Event event = Event.builder()
                .annotation(request.getAnnotation())
                .category(category)
                .requests(new ArrayList<>())
                .createdOn(now)
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .initiator(userRepository.findById(userId).orElseThrow())
                .location(location)
                .paid(request.isPaid())
                .participantLimit(request.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(request.getRequestModeration())
                .state("PENDING")
                .title(request.getTitle())
                .views(0)
                .build();
        Event store = repository.save(event);
        Integer confirmedRequests = (requestRepository.findAllByState("APPROVED")).size();
        CategoryDto categoryDto = mapper.map(store.getCategory(), CategoryDto.class);
        UserShortDto userDto = mapper.map(store.getInitiator(),UserShortDto.class);
        LocationDto locationDto = mapper.map(store.getLocation(), LocationDto.class);
        return EventMapper.toEventFullDto(store, confirmedRequests,categoryDto, userDto, locationDto);
    }
}
