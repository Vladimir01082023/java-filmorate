package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class User {
    private Integer id;
    @Email(message = "Некорректный ввод почты")
    @NonNull
    private String email;
    @NotBlank
    private final String login;
    private String name;
    @Past(message = "Дата рождения пользователя не может быть в будущем")
    @NonNull
    private final LocalDate birthday;
}
