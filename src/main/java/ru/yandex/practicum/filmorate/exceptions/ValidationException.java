package ru.yandex.practicum.filmorate.exceptions;

import java.io.IOException;

public class ValidationException extends IllegalArgumentException {
    public ValidationException(String message){
        super(message);

    }
}
