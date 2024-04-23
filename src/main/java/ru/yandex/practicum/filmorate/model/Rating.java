package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class Rating {
    private final Integer id;
    private final String name;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
