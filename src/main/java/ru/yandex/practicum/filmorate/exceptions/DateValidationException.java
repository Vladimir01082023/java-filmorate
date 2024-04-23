package ru.yandex.practicum.filmorate.exceptions;

public class DateValidationException extends RuntimeException {

    public DateValidationException(String message) {
        super(message);
    }
}