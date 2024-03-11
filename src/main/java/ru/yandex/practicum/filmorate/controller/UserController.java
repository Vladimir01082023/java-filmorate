package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private Map<Integer, User> users = new HashMap<>();
    private int usersId = 0;

    public int generateUserId() {
        return ++usersId;
    }

    @GetMapping()
    public List<User> getUsers() throws ValidationException {
        return new ArrayList<>(users.values());
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) throws ValidationException {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения пользователя не может быть в будущем");
            throw new ValidationException("Ошибка валидации");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(generateUserId());
        users.put(user.getId(), user);
        log.info("Пользователь создан");
        return user;
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user) throws ValidationException {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения пользователя не может быть в будущем");
            throw new ValidationException("Ошибка валидации");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (!users.containsKey(user.getId())) {
            log.info("Не можем обновить пользователя с id = {}, тк его нет в мапе", user.getId());
            throw new NoSuchElementException();
        }

        users.put(user.getId(), user);
        log.info("Пользователь обновлен");
        return user;
    }
}
