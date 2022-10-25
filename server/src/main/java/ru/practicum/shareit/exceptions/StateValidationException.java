package ru.practicum.shareit.exceptions;

public class StateValidationException extends RuntimeException {
    public StateValidationException(final String message) {
        super(message);
    }
}
