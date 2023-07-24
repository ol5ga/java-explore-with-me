package ru.practicum.ewm.controller.compilations;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilations.CompilationDto;
import ru.practicum.ewm.dto.compilations.NewCompilationDto;
import ru.practicum.ewm.dto.compilations.UpdateCompilationRequest;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@AllArgsConstructor
public class CompilationAdminController {
    private CompilationService service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto request){
        return service.addCompilation(request);
    }

    @DeleteMapping(path = "/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId){
        service.deleteCompilation(compId);
    }

    @PatchMapping(path = "/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,@Valid @RequestBody UpdateCompilationRequest request){
        return service.updateCompilation(compId,request);
    }
}
