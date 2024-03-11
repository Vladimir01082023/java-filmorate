package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private int filmId = 0;

    public int generateFilmId() {
        return ++filmId;
    }

    private final LocalDate minDateOfFilm = LocalDate.of(1895, Month.DECEMBER, 28);

    @GetMapping()
    public List<Film> findAll() throws ValidationException {
        return new ArrayList<>(films.values());
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(minDateOfFilm)) {
            log.error("Дата '{}' не может быть раньше '{}'", film.getReleaseDate(), minDateOfFilm);
            throw new ValidationException("Ошибка валидации");
        }
        film.setId(generateFilmId());
        films.put(film.getId(), film);
        log.info("Фильм '{}' успешно добавлен", film.getName());
        return film;
    }

    @PutMapping()
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(minDateOfFilm)) {
            log.error("Дата '{}' не может быть раньше '{}'", film.getReleaseDate(), minDateOfFilm);
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


}
