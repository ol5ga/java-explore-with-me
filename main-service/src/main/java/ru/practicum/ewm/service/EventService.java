package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventMapper;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Location;
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

        Integer confirmedRequests = requestRepository.findAllByEventAndState(event,"APPROVED").size();
        CategoryDto categoryDto = mapper.map(event.getCategory(), CategoryDto.class);
        UserShortDto userDto = mapper.map(event.getInitiator(),UserShortDto.class);
        LocationDto locationDto = mapper.map(event.getLocation(), LocationDto.class);
        return EventMapper.toEventFullDto(event, confirmedRequests,categoryDto, userDto, locationDto);
    }


}
