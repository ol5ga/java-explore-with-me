package ru.practicum.ewm.comments;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.event.AdminStateAction;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping(path = "/admin/comment")
@AllArgsConstructor
public class CommentAdminController {

    private CommentService service;

    @GetMapping
    public List<CommentAdminDto> getList(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                @RequestParam(defaultValue = "10") @Positive int size){
        return service.getList(rangeStart,rangeEnd,from,size);
    }
    @PatchMapping(path = "/{commentId}")
    public CommentDto updateStates(@PathVariable Long commentId, @RequestParam AdminStateAction action){
        return service.updateStates(commentId, action);
    }

}
