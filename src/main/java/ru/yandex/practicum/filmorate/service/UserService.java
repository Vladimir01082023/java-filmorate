package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
@Slf4j
public class UserService {

    @Autowired
    @Qualifier("inMemoryUserStorage")
    private UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User getUser(int userId) {
        return inMemoryUserStorage.getUserById(userId);
    }

    public List<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    public User create(User user) {
        return inMemoryUserStorage.create(user);
    }

    public User update(User user) {
        return inMemoryUserStorage.update(user);
    }


    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            log.warn("Id пользователя и id друга не должны совпадать");
            throw new ValidationException();
        } else {
            log.info(String.format("Получен запрос на добавление в друзья. Пользователь с id = %s хочет добавить " +
                    "пользователя с id = %s", userId, friendId));

            User user = inMemoryUserStorage.getUserById(userId);
            User friend = inMemoryUserStorage.getUserById(friendId);
            Set<Integer> userFriends = user.getFriends();
            Set<Integer> friendsOfAFriend = friend.getFriends();

            userFriends.add(friendId);
            friendsOfAFriend.add(userId);

            log.info(String.format("Пользователи %s и %s стали друзьями", user.getName(), friend.getName()));
        }
    }

    public void removeFriend(int userId, int friendId) {
        User user = inMemoryUserStorage.getUserById(userId);
        User friend = inMemoryUserStorage.getUserById(friendId);
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> friendsOfAFriend = friend.getFriends();

        userFriends.remove(friendId);
        friendsOfAFriend.remove(userId);

        log.info(String.format("Пользователи %s и %s больше не друзья друзьями", user.getName(), friend.getName()));
    }

    public List<User> getUserFriends(int userId) {
        User user = inMemoryUserStorage.getUserById(userId);
        Set<Integer> friendsIds = user.getFriends();
        Map<Integer, User> users = inMemoryUserStorage.getUserInMap();
        List<User> userFriends = new ArrayList<>();

        for (Integer friendsId : friendsIds) {
            if (users.containsKey(friendsId)) {
                userFriends.add(users.get(friendsId));
            }
        }
        return userFriends;
    }

    public List<User> getCommonFriends(int userId, int otherId) {

        User user = inMemoryUserStorage.getUserById(userId);
        User otherUser = inMemoryUserStorage.getUserById(otherId);

        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherUserFriends = otherUser.getFriends();

        List<User> commonFriends = new ArrayList<>();

        for (Integer id : userFriends) {
            if (otherUserFriends.contains(id)) {
                commonFriends.add(inMemoryUserStorage.getUserById(id));
            }
        }
        return commonFriends;
    }

}