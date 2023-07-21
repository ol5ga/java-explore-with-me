package ru.practicum.ewm.controller.event;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
public class EventPrivateController {

    private EventService eventService;

    @GetMapping(path = "/{userId}/events")
    public List<Event> getUsersEvents(@PathVariable long userId){
        return new ArrayList<Event>();
    }

    @PostMapping(path = "/{userId}/events")
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable long userId, @Valid @RequestBody NewEventDto request){

        return eventService.addEvent(userId, request);
    }
}
