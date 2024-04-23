package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void removeFilm(int filmId) {
        filmStorage.removeFilm(filmId);
    }

    public void putLike(int filmId, int userId) {
        filmStorage.putLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<User> getFilmLikes(int filmId) {
        return filmStorage.getFilmLikes(filmId);
    }

}
