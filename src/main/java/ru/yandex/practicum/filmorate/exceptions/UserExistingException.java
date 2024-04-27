package ru.yandex.practicum.filmorate.exceptions;

import java.util.NoSuchElementException;

public class UserExistingException extends NoSuchElementException {
    public UserExistingException(String message) {
        super(message);

    }
}
