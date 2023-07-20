package ru.practicum.ewm.controller.event;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.model.Event;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class EventPrivateController {

    @GetMapping(path = "/{userId}/events")
    public List<Event> getUsersEvents(@PathVariable long userId){
        return new ArrayList<Event>();
    }

    @PostMapping(path = "/{userId}/events")
    public EventFullDto addEvent(@PathVariable long userId, @Valid NewEventDto request){
        return new EventFullDto();
    }
}
