package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRowMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Review()
                .setId(rs.getLong("REVIEW_ID"))
                .setContent(rs.getString("CONTENT"))
                .setIsPositive(rs.getBoolean("IS_POSITIVE"))
                .setUserId(rs.getLong("USER_ID"))
                .setFilmId(rs.getLong("FILM_ID"));
    }
}
