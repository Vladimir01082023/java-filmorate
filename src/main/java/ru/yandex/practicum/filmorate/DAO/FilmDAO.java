package ru.yandex.practicum.filmorate.DAO;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

public class FilmDAO implements FilmStorage {
    @Override
    public List<Film> findAll() {
        return null;
    }

    @Override
    public Film create(Film film) {
        return null;
    }

    @Override
    public void remove(int id) {

    }

    @Override
    public Film update(Film film) {
        return null;
    }
}
