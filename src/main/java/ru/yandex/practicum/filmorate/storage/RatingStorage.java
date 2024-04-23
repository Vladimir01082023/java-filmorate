package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Repository
public interface RatingStorage {
    Rating getRatingById(int ratingId);

    List<Rating> getAllRatings();
}
