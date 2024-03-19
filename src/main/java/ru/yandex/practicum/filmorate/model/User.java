package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class User {
    private final Set<Integer> friends;
    private Integer id;
    @Email(message = "Некорректный ввод почты")
    @NonNull
    private String email;
    @NotBlank
    private String login;
    private String name;
    @Past(message = "Дата рождения пользователя не может быть в будущем")
    @NonNull
    private LocalDate birthday;

    public User(Integer id, String email, String login, String name, LocalDate birthday, Set<Integer> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;

        if (friends == null) {
            this.friends = new HashSet<>();
        } else {
            this.friends = friends;
        }
    }
}
