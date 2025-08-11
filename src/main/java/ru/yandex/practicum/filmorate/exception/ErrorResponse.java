package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String errorMessage;

    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "Error: " + errorMessage;
    }
}
