package ru.practicum.ewm.controller.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class RequestPrivateController {

    private RequestService service;

    @GetMapping(path = "/{userId}/requests")
    public List<ParticipationRequestDto> getUsersRequests(@PathVariable long userId){

        return service.getUsersRequests(userId);
    }

    @PostMapping(path = "/{userId}/requests")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable long userId, @RequestParam long eventId){
        return service.addRequest(userId, eventId);
    }

    @PatchMapping(path = "{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto canceledRequest(@PathVariable long userId, @RequestParam long requestId){
        return service.canceledRequest(userId,requestId);
    }
}


