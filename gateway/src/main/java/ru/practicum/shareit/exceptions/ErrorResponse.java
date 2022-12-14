package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErrorResponse {

    private String error;
    private List<String> details;

    public ErrorResponse(String message, List<String> details) {
        super();
        this.error = message;
        this.details = details;
    }
}