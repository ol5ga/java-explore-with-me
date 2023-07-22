package ru.practicum.ewm.controller.event;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.service.EventService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@AllArgsConstructor
public class EventPublicController {

    EventService service;

    @GetMapping
    public List<EventShortDto> getEvents(){
        // TODO
        return new ArrayList<>();
    }

    @GetMapping(path = "/{id}")
    public EventFullDto getEvent(@PathVariable long id){
        return service.getEvent(id);
    }
}
