package ru.yandex.practicum.filmorate.test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmStorageDaoTest {
    private final FilmService filmService;
    private final UserService userService;
    private final Rating rating = new Rating(1, "G");
    private final List<Genre> genres = List.of(new Genre(1, "Комедия"), new Genre(2, "Драма"));

    @Test
    public void shouldCreateFilm() {
        Film film = filmService.create(new Film(1, "Film", "FilmDescription",
                LocalDate.of(2000, 1, 1), 100, rating, genres));


        Film filmFromBd = filmService.getFilmById(film.getId());

        assertNotNull(filmFromBd);
        assertEquals(1, filmService.findAll().size());
        assertTrue(filmService.findAll().contains(filmFromBd));
    }

    @Test
    public void shouldUpdateFilm() {
     Film film = filmService.create(new Film(1, "Film", "FilmDescription",
                LocalDate.of(2000, 1, 1), 100, rating, genres));

        filmService.create(film);

        Film filmFromBd = filmService.getFilmById(film.getId());

        assertNotNull(filmFromBd);
        assertEquals(2, filmService.findAll().size());
        assertTrue(filmService.findAll().contains(filmFromBd));
        assertEquals(film, filmFromBd);

        film.setName("UpdatedName");
        film.setGenres(genres);

        filmService.update(film);

        assertEquals(2, filmService.findAll().size());
        assertEquals(filmService.getFilmById(film.getId()), film);

    }

    @Test
    public void shouldDeleteFilmById() {
     Film film = filmService.create(new Film(1, "Film", "FilmDescription",
                LocalDate.of(2000, 1, 1), 100, rating, genres));

        Film filmFromBd = filmService.getFilmById(film.getId());

        assertNotNull(filmFromBd);

        assertEquals(1, filmService.findAll().size());
        assertTrue(filmService.findAll().contains(filmFromBd));
        assertEquals(film, filmFromBd);

        filmService.removeFilm(film.getId());
        assertEquals(0, filmService.findAll().size());
        assertFalse(filmService.findAll().contains(filmFromBd));
    }

    @Test
    public void addLikeToFilm() {
        User user = userService.create(new User(1, "vladimir-malyshev-2014@mail.ru", "Vladimir01082023",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        Film film = filmService.create(new Film(1, "Film", "FilmDescription",
                LocalDate.of(2000, 1, 1), 100, rating, genres));


        filmService.putLike(film.getId(), user.getId());

        assertEquals(1, filmService.getFilmLikes(film.getId()).size());
        assertTrue(filmService.getFilmLikes(film.getId()).contains(user));
    }

    @Test
    public void shouldDeleteLikeFromFilm() {
        Film film = filmService.create(new Film(1, "Film", "FilmDescription",
                LocalDate.of(2000, 1, 1), 100, rating, genres));

        User user = userService.create(new User(1, "vladimir-malyshev-2014@mail.ru", "Vladimir01082023",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        filmService.putLike(user.getId(), film.getId());

        assertEquals(1, filmService.getFilmLikes(film.getId()).size());
        assertTrue(filmService.getFilmLikes(film.getId()).contains(user));

        filmService.removeLike(film.getId(), user.getId());

        assertEquals(0, filmService.getFilmLikes(film.getId()).size());
        assertFalse(filmService.getFilmLikes(film.getId()).contains(user));
    }

    @Test
    public void shouldGetTopFilmsByLikesAndTheThirdFilmMustBeOnTheFirstPlace() {
        User user1 = userService.create(new User(1, "vladimir-malyshev-2014@mail.ru", "Vladimir01",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        User user2 = userService.create(new User(2, "vladimir-malyshev-2015@mail.ru", "Vladimir02",
                "Vova", LocalDate.of(1997, 9, 11)));

        User user3 = userService.create(new User(3, "vladimir-malyshev-2016@mail.ru", "Vladimir03",
                "Volodya", LocalDate.of(1997, 9, 11)));

        userService.create(user1);
        userService.create(user2);
        userService.create(user3);

        Film film1 = filmService.create(new Film(1, "Film1", "FilmDescription1",
                LocalDate.of(2000, 1, 1), 100, rating, genres));

        Film film2 = filmService.create(new Film(2, "Film2", "FilmDescription2",
                LocalDate.of(2000, 1, 1), 100, rating, genres));

        Film film3 = filmService.create(new Film(3, "Film3", "FilmDescription3",
                LocalDate.of(2000, 1, 1), 100, rating, genres));

        filmService.create(film1);
        filmService.create(film2);
        filmService.create(film3);

        filmService.putLike(film1.getId(), user1.getId());
        filmService.putLike(film1.getId(), user2.getId());

        filmService.putLike(film2.getId(), user1.getId());

        filmService.putLike(film3.getId(), user1.getId());
        filmService.putLike(film3.getId(), user2.getId());
        filmService.putLike(film3.getId(), user3.getId());

        List<Film> topThreeFilmsByLikes = filmService.getPopularFilms(3);

        assertNotNull(topThreeFilmsByLikes);
        assertEquals(3, topThreeFilmsByLikes.size());
        assertEquals(film3, topThreeFilmsByLikes.get(0));
    }
}