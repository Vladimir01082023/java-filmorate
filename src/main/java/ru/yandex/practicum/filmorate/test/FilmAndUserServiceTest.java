package ru.yandex.practicum.filmorate.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmAndUserServiceTest {
    InMemoryFilmStorage storage;
    InMemoryUserStorage userStorage;
    FilmService filmService;
    UserService userService;

    @BeforeEach
    public void createStorage() {
        storage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(storage, userStorage);
        userService = new UserService(userStorage);
    }

    @Test
    public void createUpdateFilms() {
        Film film = storage.create(new Film(1, "Film", "Film about Me",
                LocalDate.of(2024, 01, 01), 60, new HashSet<>()));
        Film film1 = storage.getFilmById(1);
        List<Film> listOfFilms = storage.findAll();

        assertNotNull(film1, "Фильм не создаен");
        assertEquals(film, film1, "Фильм создан некорректно");
        assertNotNull(listOfFilms, "Фильмы сохранены некорректно");
        assertEquals(1, listOfFilms.size(), "Фильмы сохранены некорректно");

        Film updateFilm = storage.update(new Film(1, "FilmUpdate", "Film about Me",
                LocalDate.of(2024, 01, 01), 60, new HashSet<>()));
        List<Film> listWithUpdateFilm = storage.findAll();
        assertEquals(updateFilm, listWithUpdateFilm.get(0), "Фильмы созданы некорректно");

    }

    @Test
    public void likeTest() {
        Film film = storage.create(new Film(1, "Film", "Film about Me",
                LocalDate.of(2024, 01, 01), 60, new HashSet<>()));
        User user = userStorage.create(new User(1, "vladimir@mail.ru", "Vlad", "Vladimir",
                LocalDate.of(1997, 9, 11), new HashSet<>()));
        filmService.putLike(1, 1);
        Set<Integer> likes = film.getLikes();
        assertNotNull(likes, "Лайк не сохранен");
        assertEquals(1, likes.size(), "Лайк не сохранен");

        filmService.removeLike(1, 1);
        Set<Integer> likes1 = film.getLikes();
        assertEquals(0, likes1.size(), "Лайк не удален");

        Film filmOne = storage.create(new Film(2, "Film2", "Film about Him",
                LocalDate.of(2024, 01, 01), 60, new HashSet<>()));
        Film filmTwo = storage.create(new Film(3, "Film3", "Film about You",
                LocalDate.of(2024, 01, 01), 60, new HashSet<>()));
        Film filmThree = storage.create(new Film(4, "Film4", "Film about Us",
                LocalDate.of(2024, 01, 01), 60, new HashSet<>()));

        filmService.putLike(2, 1);
        filmService.putLike(3, 1);
        List<Film> popularFilms = filmService.getPopularFilms(2);
        assertEquals(2, popularFilms.size(), "Список популярных фильмов сохранен неверно");
    }

    @Test
    public void createUpdateUserTest() {
        User user = userStorage.create(new User(1, "vladimir@mail.ru", "Vlad", "Vladimir",
                LocalDate.of(1997, 9, 11), new HashSet<>()));

        User user1 = userStorage.getUserById(1);
        List<User> listUser = userStorage.getUsers();
        assertNotNull(user1, "Пользователь создан некорректно");
        assertEquals(user, user1, "Пользователь создаен некорректно");
        assertNotNull(listUser, "Список пользователь пусть");
        assertEquals(1, listUser.size(), "Количество пользователей неверно");

        User userUpdate = userStorage.update(new User(1, "vladimir1@mail.ru", "Vlad1", "Vladimir1",
                LocalDate.of(1997, 9, 11), new HashSet<>()));
        List<User> listWithUpdateUser = userStorage.getUsers();
        assertEquals(userUpdate, listWithUpdateUser.get(0), "Пользователь не обновлен");

    }

    @Test
    public void getFriendsTest() {
        User user = userStorage.create(new User(1, "vladimir@mail.ru", "Vlad", "Vladimir",
                LocalDate.of(1997, 9, 11), new HashSet<>()));
        User friend = userStorage.create(new User(2, "vladimir@mail.ru", "Friend", "Friend",
                LocalDate.of(1997, 9, 11), new HashSet<>()));
        userService.addFriend(1, 2);
        Set<Integer> setOfFriends = user.getFriends();
        assertNotNull(setOfFriends, "Друзья не добавлены");
        assertEquals(1, setOfFriends.size(), "Добавление в друзья некорректно");
        userService.removeFriend(1, 2);
        Set<Integer> listOfFriends = user.getFriends();
        assertEquals(0, listOfFriends.size(), "Друг не удален");
    }

}
