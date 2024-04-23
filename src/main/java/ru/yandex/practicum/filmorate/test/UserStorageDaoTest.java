package ru.yandex.practicum.filmorate.test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserStorageDaoTest {
    private final UserService userService;

    @Test
    public void shouldCreateUserAndFindUserById() {
        User user = userService.create(new User(1, "vladimir-malyshev-2014@mail.ru", "Vladimir01082023",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        User savedUser = userService.getUser(1);

        assertNotNull(savedUser);
        assertEquals(1,userService.getUsers().size());
        assertTrue(userService.getUsers().contains(savedUser));
    }

    @Test
    public void shouldUpdateAndFindUserById() {
        User user = userService.create(new User(1, "vladimir-malyshev-2014@mail.ru", "Vladimir01082023",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        User savedUser = userService.getUser(1);

        assertNotNull(savedUser);
        assertEquals(1,userService.getUsers().size());
        assertTrue(userService.getUsers().contains(savedUser));

        user.setName("UpdateName");
        user.setLogin("New");

        userService.update(user);

        assertEquals(1,userService.getUsers().size());
        assertTrue(userService.getUsers().contains(user));
        assertFalse(userService.getUsers().contains(savedUser));
    }

    @Test
    public void shouldDeleteFilmById() {
        User user = userService.create(new User(1, "vladimir-malyshev-2014@mail.ru", "Vladimir01082023",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        User savedUser = userService.getUser(user.getId());

        assertNotNull(savedUser);
        assertEquals(1,userService.getUsers().size());
        assertTrue(userService.getUsers().contains(savedUser));

        userService.deleteUserById(savedUser.getId());

        assertEquals(0,userService.getUsers().size());
        assertFalse(userService.getUsers().contains(savedUser));
    }

    @Test
    public void shouldAddUser2ToUsers1FriendsListAndUser2ShouldNotHasUser1InHisFriendsList() {
        User user = userService.create(new User(1, "vladimir-malyshev-2014@mail.ru", "Vladimir01082023",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        User user2 = userService.create(new User(2, "vladimir-malyshev-2010@mail.ru", "Vl",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        assertEquals(2, userService.getUsers().size());
        assertTrue(userService.getUsers().contains(user));
        assertTrue(userService.getUsers().contains(user2));

        userService.addFriend(user.getId(), user2.getId());

        assertTrue(userService.getUserFriends(user.getId()).contains(user2));
        assertFalse(userService.getUserFriends(user2.getId()).contains(user));
    }

    @Test
    public void shouldDeleteUser2FromUser1FriendsListAndUser2ShouldNotHasUser1InHisFriendsList() {
        User user = userService.create(new User(1, "vladimir-malyshev-2014@mail.ru", "Vladimir01082023",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        User user2 = userService.create(new User(2, "vladimir-malyshev-2010@mail.ru", "Vl",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        userService.addFriend(user.getId(), user2.getId());

        assertTrue(userService.getUserFriends(user.getId()).contains(user2));
        assertFalse(userService.getUserFriends(user2.getId()).contains(user));
        assertEquals(2, userService.getUsers().size());

        userService.removeFriend(user.getId(), user2.getId());

        assertTrue(userService.getUsers().contains(user));
        assertTrue(userService.getUsers().contains(user2));

        assertFalse(userService.getUserFriends(user.getId()).contains(user2));
        assertFalse(userService.getUserFriends(user2.getId()).contains(user));
    }

    @Test
    public void shouldGetUsersFriends() {
        User user = userService.create(new User(1, "vladimir-malyshev-2014@mail.ru", "Vladimir01082023",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        User user2 = userService.create(new User(2, "vladimir-malyshev-2010@mail.ru", "Vl",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        User user3 = userService.create(new User(3, "vladimir-malyshev-20@mail.ru", "VlA1",
                "Vladimir", LocalDate.of(1997, 9, 11)));


        userService.addFriend(user.getId(), user2.getId());
        userService.addFriend(user.getId(), user3.getId());

        List<User> userFriends = userService.getUserFriends(user.getId());

        assertNotNull(userFriends);
        assertEquals(2, userFriends.size());
        assertTrue(userFriends.contains(user2));
        assertTrue(userFriends.contains(user3));
    }

    @Test
    public void shouldGetCommonFriendsUser1AndUser2() {
        User user = userService.create(new User(1, "vladimir-malyshev-2014@mail.ru", "Vladimir01082023",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        User user2 = userService.create(new User(2, "vladimir-malyshev-2010@mail.ru", "Vl",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        User user3 = userService.create(new User(3, "vladimir-malyshev-20@mail.ru", "VlA1",
                "Vladimir", LocalDate.of(1997, 9, 11)));

        userService.addFriend(user.getId(), user3.getId());
        userService.addFriend(user2.getId(), user3.getId());

        List<User> commonUserFriends = userService.getCommonFriends(user.getId(), user2.getId());

        assertNotNull(commonUserFriends);
        assertEquals(1, commonUserFriends.size());
        assertTrue(commonUserFriends.contains(user3));
    }
}