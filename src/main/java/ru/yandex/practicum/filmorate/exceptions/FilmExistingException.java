package ru.yandex.practicum.filmorate.exceptions;


import java.util.NoSuchElementException;

public class FilmExistingException extends NoSuchElementException {
    public FilmExistingException(String message) {
        super(message);

    }
}
