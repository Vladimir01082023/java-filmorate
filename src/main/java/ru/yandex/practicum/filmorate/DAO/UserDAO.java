package ru.yandex.practicum.filmorate.DAO;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

public class UserDAO implements UserStorage {
    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public void remove(int id) {

    }

    @Override
    public User update(User user) {
        return null;
    }
}
