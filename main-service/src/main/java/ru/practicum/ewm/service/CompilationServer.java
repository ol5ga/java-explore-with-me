package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.compilations.CompilationDto;
import ru.practicum.ewm.dto.compilations.CompilationMapper;
import ru.practicum.ewm.dto.compilations.NewCompilationDto;
import ru.practicum.ewm.dto.event.EventMapper;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.user.UserMapper;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.StorageException;
import ru.practicum.ewm.model.compilations.Compilation;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@AllArgsConstructor
public class CompilationServer {
    private CompilationRepository repository;
    private EventRepository eventRepository;
    private ParticipationRequestRepository requestRepository;
    private ModelMapper mapper;
    public CompilationDto addCompilation(NewCompilationDto request) {
        List<Event> events;
        if(request.getPinned() == null){
            request.setPinned(false);
        }
        if(request.getEvents()==null || request.getEvents().isEmpty()){
            events= new ArrayList<>();
        } else{
            events = request.getEvents().stream()
                    .map(event -> eventRepository.findById(event).orElseThrow())
                    .collect(Collectors.toList());
        }
        Compilation compilation = CompilationMapper.toCompilation(request,events);
        try{
            repository.save(compilation);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Нарушение целостности данных");
        }
        List<EventShortDto> shortEvents = new ArrayList<>();
        for(Event event: compilation.getEvents()){
            Integer confirmedRequests = requestRepository.findAllByEvent(event).size();
            CategoryDto categoryDto = mapper.map(event.getCategory(), CategoryDto.class);
            UserShortDto userDto =  mapper.map(event.getInitiator(),UserShortDto.class);
            EventShortDto shortEvent = EventMapper.toEventShortDto(event,confirmedRequests,categoryDto,userDto);
            shortEvents.add(shortEvent);
        }
        return CompilationMapper.toCompilationDto(compilation, shortEvents);
    }
}
