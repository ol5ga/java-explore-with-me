package ru.practicum.ewm.controller.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.service.RequestService;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class RequestPrivateController {

    private RequestService service;

    @GetMapping(path = "/{userId}/requests")
    public ParticipationRequest getRequest(@PathVariable long userId){
        return new ParticipationRequest();
    }

    @PostMapping(path = "/{userId}/requests")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable long userId, @RequestParam long eventId){
        return service.addRequest(userId, eventId);
    }
}


