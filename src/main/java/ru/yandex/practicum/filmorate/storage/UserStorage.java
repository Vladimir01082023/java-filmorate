package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
public interface
UserStorage {
    List<User> getUsers();

    User create(User user);

    void deleteUserById(int id);

    User update(User user);

    User getUserById(int userId);

    void addFriend(int id, int friendId);

    void removeFriend(int userId, int friendId);

    List<User> getUserFriends(Integer id);

    List<User> getCommonFriends(int userId1, int userId2);
}
