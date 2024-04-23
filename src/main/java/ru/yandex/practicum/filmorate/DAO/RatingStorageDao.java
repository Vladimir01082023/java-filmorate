package ru.yandex.practicum.filmorate.DAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@Slf4j
@RequiredArgsConstructor
public class RatingStorageDao implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public Rating getRatingById(int id) {
        SqlRowSet sql = jdbcTemplate.queryForRowSet("select * from FILM_RATING where rating_id=?", id);
        if (sql.next()) {
            return new Rating(sql.getInt(1), sql.getString(2));
        }
        throw new NoSuchElementException(String.format("Нет рейтинга с id %d", id));
    }

    @Override
    public List<Rating> getAllRatings() {
        return jdbcTemplate.query("SELECT * FROM FILM_RATING", new RowMapper<Rating>() {

            public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
                Rating rating = getRating(rs, rowNum);
                return rating;
            }
        });
    }

    public Rating getRating(ResultSet rs, int rowNum) throws SQLException {
        return new Rating(rs.getInt(1), rs.getString(2));
    }
}
