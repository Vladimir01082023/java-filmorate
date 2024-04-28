package ru.yandex.practicum.filmorate.storage.DAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenreStorageDao implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public Genre getGenreById(int id) {
        SqlRowSet sql = jdbcTemplate.queryForRowSet("select * from GENRE_CATEGORY where genre_id =?", id);
        if (sql.next()) {
            return new Genre(sql.getInt(1), sql.getString(2));
        }
        throw new NoSuchElementException(String.format("Нет рейтинга с id %d", id));
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM GENRE_CATEGORY", new RowMapper<Genre>() {

            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                Genre genre = makeGenre(rs);
                return genre;
            }
        });
    }

    @Override
    public List<Genre> getFilmsGenres(int id) {
        log.info("Получен запрос на отправку всех жанров фильму с id = {}", id);

        String sql = "SELECT *\n" +
                "FROM GENRE_CATEGORY AS g\n" +
                "WHERE g.genre_id IN\n" +
                "    (SELECT fg.genre_id\n" +
                "     FROM film_genre AS fg\n" +
                "     WHERE fg.film_id = ?)";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id).stream().sorted(Comparator
                .comparing(Genre::getId)).collect(Collectors.toList());
    }


    public void deleteFilmGenres(Film film, List<Integer> removedFilmGenres) {
        log.info(String.format("Получен запрос на удаление жанров фильма %s", film.getName()));

        removedFilmGenres.forEach(genre -> {
            jdbcTemplate.update("DELETE FROM film_genre WHERE genre_id = ? ", genre);

            log.info("Жанр {} успешно удален у фильма {}", genre, film.getName());
        });
    }

    public void addGenresToFilm(Film film, List<Integer> addedFilmGenres) {
        addedFilmGenres.forEach(genre -> {
            jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) " + "VALUES (?, ?)",
                    film.getId(), genre);

            log.info("Жанр {} успешно добавлен фильму {}", genre, film.getName());
        });
    }

    @Override
    public void updateFilmGenres(Film film) {
        log.info("Получен запрос на добавление жанров фильму");

        if (film.getGenres() == null) {
            log.info("Жанры не были обновлены, тк нет изменений");
            return;
        }

        List<Integer> currentFilmGenres = getFilmsGenres(film.getId()).stream().map(Genre::getId).collect(Collectors.toList());
        Set<Integer> uniqueUpdatedFilmGenres = film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        for (Integer uniqueUpdatedFilmGenre : uniqueUpdatedFilmGenres) {
            if (uniqueUpdatedFilmGenre == 0) {
                throw new ValidationException("Id жанра не может быть 0");
            }
        }

        List<Integer> removedFilmGenres = new ArrayList<>(currentFilmGenres);
        List<Integer> addedFilmGenres = new ArrayList<>(uniqueUpdatedFilmGenres);

        removedFilmGenres.removeAll(uniqueUpdatedFilmGenres);
        addedFilmGenres.removeAll(currentFilmGenres);

        if (!removedFilmGenres.isEmpty()) {
            deleteFilmGenres(film, removedFilmGenres);
        }

        if (!addedFilmGenres.isEmpty()) {
            addGenresToFilm(film, addedFilmGenres);
        }
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre_name"));
    }
}
