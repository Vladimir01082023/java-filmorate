package ru.yandex.practicum.filmorate.storage.DAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserExistingException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserStorageDAO implements UserStorage {


    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM users", new RowMapper<User>() {

            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = getUser(rs, rowNum);
                return user;
            }
        });
    }

    @Override
    public User getUserById(int id) {
        log.info(String.format("Получен запрос на отправку пользователя с id = %s", id));

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);

        if (userRows.next()) {
            log.info(String.format("Пользователь с id = %s успешно отправлен клиенту", id));

            return new User(
                    userRows.getInt("user_id"),
                    userRows.getString("user_email"),
                    userRows.getString("user_login"),
                    userRows.getString("user_name"),
                    userRows.getDate("user_birthday").toLocalDate());

        }

        log.warn(String.format("Отсутствует пользователь с id = %s", id));

        throw new NoSuchElementException(String.format("Пользователь с id = %s отсутствует", id));
    }


    @Override
    public User create(User user) {
        log.info("Получен запрос на создание нового пользователя");
        checkPresenceUserInDataBase(user);

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_email", user.getEmail());
        parameters.put("user_login", user.getLogin());
        parameters.put("user_name", user.getName());
        parameters.put("user_birthday", user.getBirthday());

        Number generatedId = jdbcInsert.executeAndReturnKey(parameters);

        user.setId(generatedId.intValue());

        log.info("Пользователь {} успешно создан", user.getName());
        return user;
    }

    @Override
    public void deleteUserById(int id) {
        log.info(String.format("Получен запрос на удаление пользователя с id = %s", id));

        User deletedUser = getUserById(id);

        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id);

        log.info("Пользователь {} был успешно удален", deletedUser.getName());

    }

    @Override
    public User update(User user) {
        getUserById(user.getId());
        jdbcTemplate.update("UPDATE USERS SET USER_EMAIL = ?, USER_LOGIN = ?, USER_NAME = ?, USER_BIRTHDAY = ? where user_id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;

    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException("Id пользователей не должны совпадать");
        }
        getUserById(userId);
        getUserById(friendId);
        try {
            log.info(String.format("Получен запрос на добавление в друзья. Пользователь с id = %s хочет добавить " +
                    "пользователя с id = %s", userId, friendId));
            User user = getUserById(userId);
            User friend = getUserById(friendId);
            jdbcTemplate.update("INSERT INTO USER_FRIEND VALUES (?, ?, ?)", userId, friendId,
                    getStatusId("В друзьях"));
            jdbcTemplate.update("INSERT INTO USER_FRIEND VALUES (?, ?, ?)", friendId, userId,
                    getStatusId("Не в друзьях"));
            log.info(String.format("%s попал в список друзей пользователя %s", user.getName(), friend.getName()));
            log.info(String.format("%s попал в список подписчиков пользователя %s", friend.getName(), user.getName()));

        } catch (Exception e) {
            throw new ValidationException("Ошибка добавления в друзья.");
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException("Id пользователя и id друга не должны совпадать");
        }
        try {
            jdbcTemplate.update("DELETE FROM USER_FRIEND WHERE user_id = ? AND friend_id = ?", userId, friendId);
            log.info(String.format("Пользователи %s и %s больше не друзья друзьями", getUserById(userId).getName(),
                    getUserById(friendId).getName()));
        } catch (Exception e) {
            throw new NoSuchElementException("Неверные id");
        }
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        if (getUserById(id) == null) {
            throw new NoSuchElementException("Пользователя с таким id нет");
        }
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select FRIEND_ID from USER_FRIEND where USER_ID = ? AND " +
                "STATUS_ID = 2", id);
        final List<User> friends = new ArrayList<>();
        while (rowSet.next()) {
            friends.add(getUserById(rowSet.getInt(1)));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(int userId1, int userId2) {
        List<User> commonFriends = new ArrayList<>();
        for (User userFriend : getUserFriends(userId1)) {
            for (User friend : getUserFriends(userId2)) {
                if (userFriend.getId() == friend.getId()) {
                    commonFriends.add(userFriend);
                }
            }
        }
        return commonFriends;
    }

    public User getUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getInt(1), rs.getString(2), rs.getString(3),
                rs.getString(4), rs.getDate(5).toLocalDate());
    }


    public Integer getStatusId(String statusName) {
        SqlRowSet status = jdbcTemplate.queryForRowSet("select status_id from FRIENDSHIP_STATUS where STATUS_NAME = ?", statusName);
        if (status.next()) {
            return status.getInt(1);
        } else {
            throw new NoSuchElementException(String.format("%s нет в базе данных", statusName));
        }
    }

    public void checkPresenceUserInDataBase(User user) {
        List<User> users = getUsers();
        for (User user1 : users) {
            if (user1.getLogin().equals(user.getLogin()) || user1.getEmail().equals(user.getEmail())) {
                log.warn(String.format("Пользователь уже существует"));
                throw new UserExistingException("Ошибка при добавлении: пользователь уже существует!");
            }
        }
    }
}

