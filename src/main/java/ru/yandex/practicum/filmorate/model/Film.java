package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private final String description;
    @NonNull
    private LocalDate releaseDate;
    @Min(1)
    private final Integer duration;
    @NonNull
    private Rating mpa;
    private List<Genre> genres;
}
