package ru.practicum.ewm.controller.event;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
public class EventPrivateController {

    private EventService service;

    @GetMapping(path = "/{userId}/events")
    public List<EventShortDto> getUsersEvents(@PathVariable long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10")@Positive Integer size){
        return service.addUserEvents(userId,from,size);
    }

    @PostMapping(path = "/{userId}/events")
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable long userId, @Valid @RequestBody NewEventDto request){

        return service.addEvent(userId, request);
    }

    @GetMapping(path = "/{userId}/events/{eventId}")
    public EventFullDto getEvent(@PathVariable long userId, @PathVariable long eventId){
        return service.getId(userId,eventId);
    }

    @PatchMapping(path = "/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable long userId, @PathVariable long eventId,@RequestBody UpdateEventUserRequest request ){
        return service.updateEvent(userId,eventId,request);
    }
}
