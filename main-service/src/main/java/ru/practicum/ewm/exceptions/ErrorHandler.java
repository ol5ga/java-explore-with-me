package ru.practicum.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse handleChangeException(final StorageException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse handleConflictException(final ConflictException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse handleThrowable(final Throwable e) {
        log.debug("Получен статус 500 Interval server error {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }
}