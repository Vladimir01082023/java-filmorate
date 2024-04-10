package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    @Autowired
    @Qualifier("inMemoryFilmStorage")
private final FilmStorage inMemoryFilmStorage;
    @Autowired
    @Qualifier("inMemoryUserStorage")
private final UserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public List<Film> findAll() {
        return inMemoryFilmStorage.findAll();
    }

    public Film getFilmById(int id) {
        return inMemoryFilmStorage.getFilmById(id);
    }

    public Film create(Film film) {
        return inMemoryFilmStorage.create(film);
    }

    public Film update(Film film) {
        return inMemoryFilmStorage.update(film);
    }

    public void putLike(int filmId, int userId) {
        Film film = inMemoryFilmStorage.getFilmById(filmId);
        User user = inMemoryUserStorage.getUserById(userId);
        Set<Integer> likes = film.getLikes();
        likes.add(userId);
        log.info(String.format("Пользователь %s поставил лайк фильму %s", user.getName(), film.getName()));
    }

    public void removeLike(int filmId, int userId) {
        Film film = inMemoryFilmStorage.getFilmById(filmId);
        User user = inMemoryUserStorage.getUserById(userId);
        Set<Integer> likes = film.getLikes();
        likes.remove(userId);
        log.info(String.format("Пользователь %s удалил свой лайк фильму %s", user.getName(), film.getName()));
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilm = inMemoryFilmStorage.findAll().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size() * -1))
                .limit(count)
                .collect(Collectors.toList());
        return popularFilm;
    }
}
