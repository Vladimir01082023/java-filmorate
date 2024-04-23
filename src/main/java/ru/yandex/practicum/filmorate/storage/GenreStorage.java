package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
public interface GenreStorage {
    Genre getGenreById(int id);

    List<Genre> getAllGenres();

    List<Genre> getFilmsGenres(int id);

    void updateFilmGenres(Film film);
}
