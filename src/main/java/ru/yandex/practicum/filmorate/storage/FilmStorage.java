package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
public interface FilmStorage {
    List<Film> findAll();

    Film create(Film film);

    void removeFilm(int id);

    Film update(Film film);

    Film getFilmById(int filmId);

    void putLike(int filmId, int userId);

    List<Film> getPopularFilms(int count);

    void removeLike(int filmId, int userId);

    List<User> getFilmLikes(int filmId);
}
