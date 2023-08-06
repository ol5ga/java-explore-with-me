package ru.practicum.ewm.dto.compilations;

import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.model.compilations.Compilation;
import ru.practicum.ewm.model.event.Event;

import java.util.List;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto request, List<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(request.getPinned())
                .title(request.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(events)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
