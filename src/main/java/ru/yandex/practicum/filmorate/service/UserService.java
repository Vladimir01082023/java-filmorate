package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;


    public User getUser(int userId) {
        return userStorage.getUserById(userId);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void deleteUserById(int id) {
        userStorage.deleteUserById(id);
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(int userId) {
        return userStorage.getUserFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int userId2) {
        return userStorage.getCommonFriends(userId, userId2);
    }
}

