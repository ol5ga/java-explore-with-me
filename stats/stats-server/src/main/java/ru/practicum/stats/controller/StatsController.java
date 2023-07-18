package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.stats.EndpointHit;
import ru.practicum.dto.stats.ViewStats;
import ru.practicum.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService service;

    @PostMapping(path = "/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void hit(@RequestBody EndpointHit hit) {

        service.add(hit);
    }

    @GetMapping(path = "/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") Boolean unique
    ) {
        return service.getStats(start, end, uris, unique);
    }
}
