package ru.practicum.ewm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class StorageException extends RuntimeException {

    public StorageException(final String message) {

        super(message);
    }
}
