package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ReviewRepository.class, ReviewRowMapper.class})
class ReviewRepositoryTest {

    private final ReviewRepository repository;
    private final JdbcTemplate jdbc;

    @Test
    void findAll() {
        List<Review> reviews = repository.findAll();
        assertEquals(2, reviews.size());
    }

    @Test
    void save() {
        //clear REVIEWS TABLE;
        jdbc.update("DELETE FROM REVIEWS");

        Review review = new Review()
                .setId(null)
                .setContent("bad")
                .setIsPositive(true)
                .setFilmId(1L)
                .setUserId(1L);

        Review save = repository.save(review);

        assertNotNull(save.getId());

        Map<String, Object> dbReview = jdbc.queryForMap(
                "SELECT * FROM REVIEWS WHERE FILM_ID = ?",
                save.getId()
        );
        assertEquals("bad", dbReview.get("CONTENT"));
        assertEquals(Integer.valueOf("1"), dbReview.get("USER_ID"));
        assertEquals(Integer.valueOf("1"), dbReview.get("FILM_ID"));
        assertEquals(Boolean.valueOf("TRUE"), dbReview.get("IS_POSITIVE"));

    }

    @Test
    void update() {
        Review review = new Review()
                .setId(1L)
                .setContent("very bad")
                .setIsPositive(false)
                .setFilmId(1L)
                .setUserId(1L);

        Review updated = repository.update(review);

        Map<String, Object> dbReview = jdbc.queryForMap(
                "SELECT * FROM REVIEWS WHERE FILM_ID = ?",
                updated.getId()
        );
        assertEquals("very bad", dbReview.get("CONTENT"));
        assertEquals(Integer.valueOf("1"), dbReview.get("USER_ID"));
        assertEquals(Integer.valueOf("1"), dbReview.get("FILM_ID"));
        assertEquals(Boolean.valueOf("FALSE"), dbReview.get("IS_POSITIVE"));
    }

    @Test
    void update_whenReviewIsNotFound() {
        Review review = new Review()
                .setId(23L)
                .setContent("very bad")
                .setIsPositive(false)
                .setFilmId(1L)
                .setUserId(1L);

        assertThatThrownBy(() -> repository.update(review)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_whenReviewAlreadyExistsWithDifferentId() {
        Review review = new Review()
                .setId(1L)
                .setContent("very bad")
                .setIsPositive(false)
                .setFilmId(2L)
                .setUserId(1L);

        assertThatThrownBy(() -> repository.update(review)).isInstanceOf(ValidationException.class);
    }

    @Test
    void findById_whenReviewExists() {
        Optional<Review> byId = repository.findById(1L);
        assertThat(byId).isPresent().hasValueSatisfying(review -> {
            assertThat(review).hasFieldOrPropertyWithValue("id", 1L);
            assertThat(review).hasFieldOrPropertyWithValue("content", "content");
            assertThat(review).hasFieldOrPropertyWithValue("isPositive", true);
            assertThat(review).hasFieldOrPropertyWithValue("filmId", 1L);
            assertThat(review).hasFieldOrPropertyWithValue("userId", 1L);
        });
    }

    @Test
    void findById_whenReviewNotExists() {
        Optional<Review> byId = repository.findById(3L);
        assertThat(byId).isEmpty();
    }

    @Test
    void deleteById_whenTrue() {
        boolean deleted = repository.deleteById(1L);
        assertThat(deleted).isTrue();
    }

    @Test
    void deleteById_whenFalse() {
        boolean deleted = repository.deleteById(44L);
        assertThat(deleted).isFalse();
    }

    @Test
    void existById_whenTrue() {
        boolean existed = repository.existById(1L);
        assertThat(existed).isTrue();
    }

    @Test
    void existById_whenFalse() {
        boolean existed = repository.existById(8L);
        assertThat(existed).isFalse();
    }

    @Test
    void getReviewsByFilmId() {
        List<Review> reviewsByFilmId = repository.getReviewsByFilmId(1, 10);
        assertEquals(1, reviewsByFilmId.size());
        assertEquals(1L, reviewsByFilmId.getFirst().getFilmId());
        assertTrue(reviewsByFilmId.getFirst().getIsPositive());
    }

    @Test
    void addReviewLike_whenNotExists() {
        boolean likeAdded = repository.addReviewLike(1, 1);
        assertThat(likeAdded).isTrue();
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = 1 AND USER_ID = 1",
                Integer.class);
        assertThat(rows).isEqualTo(1);

    }

    @Test
    void addReviewLike_whenDislikeExists() {
        addDislike(1);
        boolean likeAdded = repository.addReviewLike(1, 1);
        assertThat(likeAdded).isTrue();
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = 1 AND USER_ID = 1",
                Integer.class);
        assertThat(rows).isEqualTo(1);
    }

    @Test
    void addReviewLike_whenLikeExists() {
        addLike(1);
        boolean likeAdded = repository.addReviewLike(1, 1);
        assertThat(likeAdded).isFalse();
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = 1 AND USER_ID = 1",
                Integer.class);
        assertThat(rows).isEqualTo(1);
    }

    @Test
    void addReviewDislike_whenNotExists() {
        boolean dislikeAdded = repository.addReviewDislike(1, 1);
        assertThat(dislikeAdded).isTrue();
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = 1 AND USER_ID = 1",
                Integer.class);
        assertThat(rows).isEqualTo(1);
    }

    @Test
    void addReviewDislike_whenLikeExists() {
        addLike(1);
        boolean dislikeAdded = repository.addReviewDislike(1, 1);
        assertThat(dislikeAdded).isTrue();
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = 1 AND USER_ID = 1",
                Integer.class);
        assertThat(rows).isEqualTo(1);
    }

    @Test
    void addReviewDislike_whenDislikeExists() {
        addDislike(1);
        boolean dislikeAdded = repository.addReviewDislike(1, 1);
        assertThat(dislikeAdded).isFalse();
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = 1 AND USER_ID = 1",
                Integer.class);
        assertThat(rows).isEqualTo(1);
    }

    @Test
    void deleteLikeDislikeRow_whenLikeExists() {
        addLike(1);
        boolean deleted = repository.deleteLikeDislikeRow(1, 1);
        assertThat(deleted).isTrue();
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = 1 AND USER_ID = 1",
                Integer.class);
        assertThat(rows).isEqualTo(0);
    }

    @Test
    void deleteLikeDislikeRow_whenDislikeExists() {
        addDislike(1);
        boolean deleted = repository.deleteLikeDislikeRow(1, 1);
        assertThat(deleted).isTrue();
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = 1 AND USER_ID = 1",
                Integer.class);
        assertThat(rows).isEqualTo(0);
    }

    @Test
    void deleteLikeDislikeRow_whenNotExists() {
        boolean deleted = repository.deleteLikeDislikeRow(1, 1);
        assertThat(deleted).isFalse();
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = 1 AND USER_ID = 1",
                Integer.class);
        assertThat(rows).isEqualTo(0);
    }

    @Test
    void deleteDislikeRow_whenLikeExists() {
        addLike(1);
        boolean deleted = repository.deleteDislikeRow(1, 1);
        assertThat(deleted).isFalse();
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = 1 AND USER_ID = 1",
                Integer.class);
        assertThat(rows).isEqualTo(1);
    }

    @Test
    void deleteDislikeRow_whenDislikeExists() {
        addDislike(1);
        boolean deleted = repository.deleteDislikeRow(1, 1);
        assertThat(deleted).isTrue();
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = 1 AND USER_ID = 1",
                Integer.class);
        assertThat(rows).isEqualTo(0);
    }

    @Test
    void deleteDislikeRow_whenNotExists() {
        boolean deleted = repository.deleteDislikeRow(1, 1);
        assertThat(deleted).isFalse();
        Integer rows = jdbc.queryForObject("SELECT COUNT(*) FROM REVIEW_LIKES WHERE REVIEW_ID = 1 AND USER_ID = 1",
                Integer.class);
        assertThat(rows).isEqualTo(0);
    }

    @Test
    void calculateUsefulByReviewId() {
        int useful1 = repository.calculateUsefulByReviewId(1);
        assertThat(useful1).isEqualTo(0);

        addDislike(1);

        int useful2 = repository.calculateUsefulByReviewId(1);
        assertThat(useful2).isEqualTo(-1);

        jdbc.update("DELETE FROM REVIEW_LIKES");
        addLike(1);

        int useful3 = repository.calculateUsefulByReviewId(1);
        assertThat(useful3).isEqualTo(1);
    }

    @BeforeEach
    void initBd() {
        jdbc.update("INSERT INTO PUBLIC.FILMS (FILM_ID,NAME,DESCRIPTION,RELEASE_DATE,DURATION,MPA_ID)\n" +
                    "\tVALUES (1,'film','desc','2020-01-01',121,1)");
        jdbc.update("INSERT INTO PUBLIC.FILMS (FILM_ID,NAME,DESCRIPTION,RELEASE_DATE,DURATION,MPA_ID)\n" +
                    "\tVALUES (2,'film2','desc','2020-01-01',121,1)");
        jdbc.update("INSERT INTO PUBLIC.USERS (USER_ID,EMAIL,LOGIN,NAME,BIRTHDAY)\n" +
                    "\tVALUES (1,'email','login','name','2000-01-01')");
        jdbc.update("INSERT INTO PUBLIC.REVIEWS (REVIEW_ID,CONTENT,IS_POSITIVE,USER_ID,FILM_ID)\n" +
                    "\tVALUES (1,'content',true,1,1)");
        jdbc.update("INSERT INTO PUBLIC.REVIEWS (REVIEW_ID,CONTENT,IS_POSITIVE,USER_ID,FILM_ID)\n" +
                    "\tVALUES (2,'content',true,1,2)");
    }

    @AfterEach
    void clearTables() {
        jdbc.update("DELETE FROM REVIEWS");
        jdbc.update("DELETE FROM USERS");
        jdbc.update("DELETE FROM FILMS");
        jdbc.update("DELETE FROM REVIEW_LIKES");
    }

    private void addLike(long userId) {
        jdbc.update("INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID, IS_DISLIKE) VALUES (1,?,FALSE)", userId);
    }

    private void addDislike(long userId) {
        jdbc.update("INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID, IS_DISLIKE) VALUES (1,?,TRUE)", userId);
    }
}