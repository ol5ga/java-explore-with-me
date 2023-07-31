package ru.practicum.ewm.controller.compilations;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilations.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@AllArgsConstructor
@Validated
public class ComplicationPublicController {
    private CompilationService service;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {

        return service.getCompilations(pinned, from, size);
    }

    @GetMapping(path = "/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        return service.getCompilation(compId);
    }
}
