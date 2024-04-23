package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping()
    public List<User> getUsers() throws ValidationException {
        return new ArrayList<>(userService.getUsers());
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable int userId) {
        return userService.getUser(userId);
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) throws ValidationException {
        return userService.create(user);
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user) throws ValidationException {
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendsId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendsId) {
        userService.addFriend(id, friendsId);
    }

    @DeleteMapping("/{id}/friends/{friendsId}")
    public void deleteFriends(@PathVariable int id, @PathVariable int friendsId) {
        userService.removeFriend(id, friendsId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
