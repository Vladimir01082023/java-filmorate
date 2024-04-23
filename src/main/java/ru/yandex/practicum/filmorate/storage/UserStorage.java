package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
public interface UserStorage {
    List<User> getUsers();

    User create(User user);

    void remove(int id);

    User update(User user);
}
