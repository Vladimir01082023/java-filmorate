package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
public class Film {
    private final Set<Integer> likes;
    private Integer id;
    @NotBlank(message = "Название фильмы не может быть пустым.")
    private final String name;
    @Size(max = 200, message = "Максимальная длина текста - 200 символов.")
    private final String description;
    @NonNull
    private LocalDate releaseDate;
    @Min(1)
    private final Integer duration;
    @NonNull
    private List<Integer> genre;
    @NonNull
    private Integer ratingId;

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration, Set<Integer> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.releaseDate = releaseDate;

        if (likes == null) {
            this.likes = new HashSet<>();
        } else {
            this.likes = likes;
        }
    }
    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration,
                Set<Integer> likes, List<Integer> genre, Integer ratingId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.genre = new ArrayList<>();
        this.ratingId = ratingId;

        if (likes == null) {
            this.likes = new HashSet<>();
        } else {
            this.likes = likes;
        }
    }
}
