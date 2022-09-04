package ru.practicum.shareit.exceptions;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(final String message) {
        super(message);
    }
}
