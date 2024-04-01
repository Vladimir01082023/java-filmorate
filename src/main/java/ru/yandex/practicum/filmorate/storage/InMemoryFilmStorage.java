package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Component("inMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 0;

    private int generateFilmId() {
        return ++filmId;
    }

    private static final LocalDate MIN_DATE_OF_FILM = LocalDate.of(1895, Month.DECEMBER, 28);


    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }


    public Film create(Film film) throws ValidationException {
        if (timeIsBefore(film.getReleaseDate())) {
            log.error("Дата '{}' не может быть раньше '{}'", film.getReleaseDate(), MIN_DATE_OF_FILM);
            throw new ValidationException("Ошибка валидации");
        }
        film.setId(generateFilmId());
        films.put(film.getId(), film);
        log.info("Фильм '{}' успешно добавлен", film.getName());
        return film;
    }


    public Film update(Film film) throws ValidationException {
        if (timeIsBefore(film.getReleaseDate())) {
            log.error("Дата '{}' не может быть раньше '{}'", film.getReleaseDate(), MIN_DATE_OF_FILM);
            throw new ValidationException("Ошибка валидации");
        }
        if (!films.containsKey(film.getId())) {
            log.info("Не можем обновить фильм с id = {}, тк его нет в мапе", film.getId());
            throw new NoSuchElementException();
        }

        films.put(film.getId(), film);
        log.info("Фильм {} успешно обновлен", film.getName());
        return film;
    }

    public void remove(int id) {
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new NoSuchElementException();
        }
    }

    public Film getFilmById(int filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            throw new NoSuchElementException();
        }
    }

    private boolean timeIsBefore(LocalDate localDate) {
        return localDate.isBefore(MIN_DATE_OF_FILM);
    }
}
