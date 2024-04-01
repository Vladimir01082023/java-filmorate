package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int usersId = 0;

    private int generateUserId() {
        return ++usersId;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public Map<Integer, User> getUserInMap() {
        return users;
    }


    public User create(User user) throws ValidationException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(generateUserId());
        users.put(user.getId(), user);
        log.info("Пользователь создан");
        return user;
    }


    public User update(User user) throws ValidationException {
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

    public void remove(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new NoSuchElementException();
        }
    }

    public User getUserById(int usersId) {
        if (users.containsKey(usersId)) {
            return users.get(usersId);
        } else {
            throw new NoSuchElementException();
        }
    }
}
