package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
public interface FilmStorage {
    List<Film> findAll();

    Film create(Film film);

    void remove(int id);

    Film update(Film film);
}
