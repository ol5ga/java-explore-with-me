package ru.practicum.ewm.controller.compilations;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilations.CompilationDto;
import ru.practicum.ewm.dto.compilations.NewCompilationDto;
import ru.practicum.ewm.service.CompilationServer;

@RestController
@RequestMapping(path = "/admin/compilations")
@AllArgsConstructor
public class CompilationAdminController {
    private CompilationServer service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody NewCompilationDto request){
        return service.addCompilation(request);
    }
}
