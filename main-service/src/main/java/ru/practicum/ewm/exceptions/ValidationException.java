package ru.practicum.ewm.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(final String message) {

        super(message);
    }
}
