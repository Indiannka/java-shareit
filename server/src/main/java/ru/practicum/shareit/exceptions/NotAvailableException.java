package ru.practicum.shareit.exceptions;

public class NotAvailableException extends RuntimeException {
    public NotAvailableException(final String message) {
        super(message);
    }
}
