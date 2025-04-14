package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {
    private final JdbcTemplate jdbc;
    private final ReviewRowMapper mapper;

    private static final String DELETE_LIKE_DISLIKE_ROW = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
    private static final String IS_REVIEW_LIKE_DISLIKE_EXIST = """
            SELECT EXISTS(SELECT 1 FROM REVIEW_LIKES
            WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_DISLIKE = ?)""";
    private static final String ADD_REVIEW_LIKE_DISLIKE_ROW = """
            INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID, IS_DISLIKE) VALUES (?,?,?)""";
    private static final String UPDATE_REVIEW_LIKE_ROW = """
            UPDATE REVIEW_LIKES SET IS_DISLIKE = ? WHERE REVIEW_ID = ? AND USER_ID = ?""";

    public List<Review> findAll() {
        String findAllQuery = "SELECT * FROM REVIEWS";
        return jdbc.query(findAllQuery, mapper);
    }

    public Review save(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertReviewQuery = """
                INSERT INTO REVIEWS(CONTENT, IS_POSITIVE, USER_ID, FILM_ID) VALUES (?,?,?,?)""";
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertReviewQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);

        review.setId((Objects.requireNonNull(keyHolder.getKey()).longValue()));
        return review;
    }

    public Review update(Review review) {
        Long id = review.getId();
        if (!existById(id)) throw new NotFoundException("There is no review with id=" + id);
        String updateReviewQuery = """
                UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? WHERE REVIEW_ID = ?""";
        try {
            jdbc.update(updateReviewQuery,
                    review.getContent(),
                    review.getIsPositive(),
                    id);
        } catch (DataAccessException e) {
            throw new ValidationException("User with id=" + review.getUserId() +
                                          " already have another review for film with filmId=" + review.getFilmId());
        }

        return findById(review.getId()).orElseThrow(() -> new NotFoundException("There is no review with id=" + id));
    }

    public Optional<Review> findById(long id) {
        try {
            String findReviewByIdQuery = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
            Review review = jdbc.queryForObject(findReviewByIdQuery, mapper, id);
            return Optional.ofNullable(review);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean deleteById(long id) {
        String deleteById = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        int updatedRows = jdbc.update(deleteById, id);
        return updatedRows > 0;
    }

    public boolean existById(long id) {
        String isReviewExist = "SELECT EXISTS(SELECT 1 FROM REVIEWS WHERE REVIEW_ID = ?)";
        return jdbc.queryForObject(isReviewExist, Boolean.class, id);
    }

    public List<Review> getReviewsByFilmId(long filmId, int count) {
        String findAllByFilmIdQuery = "SELECT * FROM REVIEWS WHERE FILM_ID = ? LIMIT ?";
        return jdbc.query(findAllByFilmIdQuery, mapper, filmId, count);
    }

    public boolean addReviewLike(long id, long userId) {
        if (ifLikeExists(id, userId)) {
            return false;
        } else if (ifDislikeExists(id, userId)) {
            jdbc.update(UPDATE_REVIEW_LIKE_ROW, false, id, userId);
            return true;
        } else {
            jdbc.update(ADD_REVIEW_LIKE_DISLIKE_ROW, id, userId, false);
            return true;
        }
    }

    public boolean addReviewDislike(long id, long userId) {
        if (ifDislikeExists(id, userId)) {
            return false;
        } else if (ifLikeExists(id, userId)) {
            jdbc.update(UPDATE_REVIEW_LIKE_ROW, true, id, userId);
            return true;
        } else {
            jdbc.update(ADD_REVIEW_LIKE_DISLIKE_ROW, id, userId, true);
            return true;
        }
    }

    public boolean deleteLikeDislikeRow(long id, long userId) {
        int deleted = jdbc.update(DELETE_LIKE_DISLIKE_ROW, id, userId);
        return deleted > 0;
    }

    public boolean deleteDislikeRow(long id, long userId) {
        if (ifDislikeExists(id, userId)) {
            jdbc.update(DELETE_LIKE_DISLIKE_ROW, id, userId);
            return true;
        }
        return false;
    }

    public int calculateUsefulByReviewId(long id) {
        String calculateUsefulByReviewId = """
                SELECT
                	SUM(CASE WHEN IS_DISLIKE = FALSE THEN 1 ELSE 0 END) -
                	SUM(CASE WHEN IS_DISLIKE = TRUE THEN 1 ELSE 0 END) AS USEFUL
                FROM REVIEW_LIKES WHERE REVIEW_ID = ?""";
        Integer useful = jdbc.queryForObject(calculateUsefulByReviewId, Integer.class, id);
        return useful == null ? 0 : useful;
    }

    private boolean ifDislikeExists(long id, long userId) {
        return jdbc.queryForObject(IS_REVIEW_LIKE_DISLIKE_EXIST, Boolean.class, id, userId, true);
    }

    private boolean ifLikeExists(long id, long userId) {
        return jdbc.queryForObject(IS_REVIEW_LIKE_DISLIKE_EXIST, Boolean.class, id, userId, false);
    }
}
