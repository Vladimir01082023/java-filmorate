package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

@Repository
public interface UserStorage {
    User getUserById(int userId);
    Map<Integer, User> getUserInMap();
    List<User> getUsers();

    User create(User user);

    void remove(int id);

    User update(User user);
}
