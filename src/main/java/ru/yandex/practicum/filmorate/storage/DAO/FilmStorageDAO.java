package ru.yandex.practicum.filmorate.storage.DAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DateValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserExistingException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmStorageDAO implements FilmStorage {


    private final JdbcTemplate jdbcTemplate;
    private final RatingStorageDao ratingStorageDao;
    private final UserStorageDAO userStorageDAO;
    private final GenreStorageDao genreStorageDao;


    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM film", new RowMapper<Film>() {

            public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                Film film = getFilm(rs, rowNum);
                return film;
            }
        });
    }

    @Override
    public Film create(Film film) {
        log.info("Получен запрос на создание нового фильма");
        checkDateValidation(film.getReleaseDate());
        checkPresenceFilmInDataBase(film);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("film_name", film.getName());
        parameters.put("film_description", film.getDescription());
        parameters.put("film_release_date", film.getReleaseDate());
        parameters.put("film_duration", film.getDuration());
        parameters.put("film_rating_id", film.getMpa().getId());

        Number generatedId = jdbcInsert.executeAndReturnKey(parameters);

        film.setId(generatedId.intValue());
        genreStorageDao.updateFilmGenres(film);

        log.info(String.format("Фильм создан: %s", film.getName()));
        return film;

    }


    @Override
    public void removeFilm(int id) {
        log.info(String.format("Получен запрос на удаление фильма с id = %s", id));

        Film deletedFilm = getFilmById(id);

        jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", id);

        log.info("Фильм {} был успешно удален", deletedFilm.getName());

    }

    @Override
    public Film update(Film film) {
        for (Film film1 : getAllFilms()) {
            if (film.getId() == film1.getId()) {
                jdbcTemplate.update("UPDATE FILM SET FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?, " +
                                "FILM_DURATION = ?, FILM_RATING_ID = ? where FILM_ID = ?", film.getName(), film.getDescription(),
                        film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
                log.info(String.format("%s успешно обновлен", film.getName()));
                genreStorageDao.updateFilmGenres(film);
                return film;
            }
        }
        throw new NoSuchElementException(String.format("%s отсутствует в базе данных", film.getName()));
    }

    @Override
    public Film getFilmById(int filmId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE FILM_ID = ?", filmId);
        List<Integer> filmIds = new ArrayList<>();
        for (Film film1 : getAllFilms()) {
            filmIds.add(film1.getId());
        }
        if (filmIds.contains(filmId)) {
            if (rowSet.next()) {
                log.info(String.format("Фильм с id %d найден", filmId));
                return new Film(
                        rowSet.getInt("film_id"),
                        rowSet.getString("film_name"),
                        rowSet.getString("film_description"),
                        rowSet.getDate("film_release_date").toLocalDate(),
                        rowSet.getInt("film_duration"),
                        ratingStorageDao.getRatingById(rowSet.getInt("film_rating_id")),
                        genreStorageDao.getFilmsGenres(filmId));
            }
        }
        throw new NoSuchElementException("Фильм с данным id не найден");
    }

    public Film getFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(rs.getInt(1), rs.getString(2), rs.getString(3),
                rs.getDate(4).toLocalDate(), rs.getInt(5),
                ratingStorageDao.getRatingById(rs.getInt("film_rating_id")),
                genreStorageDao.getFilmsGenres(rs.getInt(1)));
    }


    public List<Film> getPopularFilms(int count) {

        String sql = "SELECT f.*\n" +
                "FROM film AS f\n" +
                "LEFT JOIN film_like AS fl ON f.film_id = fl.film_id\n" +
                "GROUP BY f.film_id, f.film_name, f.film_description, f.film_release_date, f.film_duration, f.film_rating_id\n" +
                "ORDER BY COUNT(fl.user_id) DESC\n" +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getFilm(rs, rowNum), count);

    }

    @Override
    public List<User> getFilmLikes(int id) {
        log.info("Получен запрос на отправление всех лайков от людей фильму с id = {}", id);

        getFilmById(id);

        String sql = "\n" +
                "SELECT *\n" +
                "FROM users AS u\n" +
                "WHERE u.user_id IN\n" +
                "    (SELECT user_id\n" +
                "     FROM film_like AS fl\n" +
                "     WHERE fl.film_id = ?)";

        log.info("Фильму с id = {} успешно отправлен всех лайков от людей", id);

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("user_email"),
                rs.getString("user_login"),
                rs.getString("user_name"),
                rs.getDate("user_birthday").toLocalDate());
    }

    @Override
    public void putLike(int filmId, int userId) {
        getFilmById(filmId);
        userStorageDAO.getUserById(userId);
        jdbcTemplate.update("INSERT INTO FILM_LIKE VALUES ( ?, ? )", filmId, userId);
        log.info(String.format("%s поставил лайк фильму : %s", userStorageDAO.getUserById(userId), getFilmById(filmId)));
    }

    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM FILM_LIKE WHERE  FILM_ID = ? AND USER_ID = ?", filmId, userId);
        log.info(String.format("%s удалил свой лайк фильму : %s", userStorageDAO.getUserById(userId), getFilmById(filmId)));
    }


    private void checkDateValidation(LocalDate localDate) {
        if (localDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("При создании фильма поле дата-релиза объекта Film не прошло валидацию");

            throw new DateValidationException("Дата фильма должна быть не менше 1895-12-28");
        }
    }

    public void checkPresenceFilmInDataBase(Film film) {
        List<Film> films = getAllFilms();
        for (Film film1 : films) {
            if (film1.getName().toLowerCase().equals(film.getName().toLowerCase()) &&
                    film1.getDescription().toLowerCase().equals(film.getDescription().toLowerCase()) &&
                    film1.getReleaseDate().isEqual(film.getReleaseDate()) && film1.getDuration().equals(film.getDuration()) &&
                    film1.getMpa().equals(film.getMpa()) &&
                    film1.getGenres().stream().map(genre -> genre.getId()).collect(Collectors.toList()).equals
                            (film.getGenres().stream().map(genre -> genre.getId()).collect(Collectors.toList()))) {
                throw new UserExistingException("Ошибка при добавлении фильмы: фильм уже существует!");
            }
        }
    }
}
