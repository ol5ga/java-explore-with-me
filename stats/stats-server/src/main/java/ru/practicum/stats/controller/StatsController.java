package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.stats.EndpointHit;
import ru.practicum.dto.stats.ViewStats;
import ru.practicum.stats.service.StatsService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
//@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StatsController {

    // private static final DateimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss)
    private final StatsService service;

    @PostMapping(path = "/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void hit(@RequestBody EndpointHit hit){
        service.add(hit);
    }

    @GetMapping(path = "/stat")
    public ResponseEntity<List<ViewStats>> getStats(@RequestParam @NotNull String start,
                                                    @RequestParam @NotNull String end,
                                                    @RequestParam (required = false) List<String> uris,
                                                    @RequestParam(required = false) Integer hits,
                                                    @RequestParam(defaultValue = "false") Boolean unique
                                                    ){
        return null;
    }


}
