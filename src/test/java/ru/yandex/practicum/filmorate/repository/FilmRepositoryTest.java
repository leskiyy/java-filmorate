package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class, FilmRowMapper.class, GenreRowMapper.class, DirectorRowMapper.class})
class FilmRepositoryTest {

    private final FilmRepository repository;
    private final JdbcTemplate jdbc;

    @Test
    void findAll() {
        List<Film> films = repository.findAll();
        assertEquals(1, films.size());
    }

    @Test
    void save() {
        clearTables();
        Film film = new Film()
                .setId(null)
                .setName("test")
                .setDescription("description")
                .setReleaseDate(LocalDate.of(2020, 1, 1))
                .setDuration(120)
                .setMpa(5);

        Film save = repository.save(film);

        assertNotNull(save.getId());

        Map<String, Object> dbFilm = jdbc.queryForMap(
                "SELECT * FROM FILMS WHERE FILM_ID = ?",
                save.getId()
        );
        assertEquals("test", dbFilm.get("NAME"));
        assertEquals("description", dbFilm.get("DESCRIPTION"));
        assertEquals(java.sql.Date.valueOf("2020-01-01"), dbFilm.get("RELEASE_DATE"));
        assertEquals(Integer.valueOf("120"), dbFilm.get("DURATION"));
        assertEquals(Integer.valueOf("5"), dbFilm.get("MPA_ID"));

    }

    @Test
    void update() {
        Film film = new Film()
                .setId(1L)
                .setName("updatedName")
                .setDescription("updated description")
                .setReleaseDate(LocalDate.of(2021, 1, 1))
                .setDuration(120)
                .setMpa(3);

        Film update = repository.update(film);

        Map<String, Object> dbFilm = jdbc.queryForMap(
                "SELECT * FROM FILMS WHERE FILM_ID = ?",
                update.getId()
        );

        assertEquals("updatedName", dbFilm.get("NAME"));
        assertEquals("updated description", dbFilm.get("DESCRIPTION"));
        assertEquals(java.sql.Date.valueOf("2021-01-01"), dbFilm.get("RELEASE_DATE"));
        assertEquals(Integer.valueOf("120"), dbFilm.get("DURATION"));
        assertEquals(Integer.valueOf("3"), dbFilm.get("MPA_ID"));

    }

    @Test
    void findById_whenFilmExists() {
        Optional<Film> byId = repository.findById(1);

        assertThat(byId).isPresent().hasValueSatisfying(film -> {
            assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
            assertThat(film).hasFieldOrPropertyWithValue("name", "film");
            assertThat(film).hasFieldOrPropertyWithValue("releaseDate",
                    LocalDate.of(2020, 1, 1));
            assertThat(film).hasFieldOrPropertyWithValue("duration", 121);
            assertThat(film).hasFieldOrPropertyWithValue("mpa", 1);
        });
    }

    @Test
    void findById_whenFilmNotExists() {
        Optional<Film> byId = repository.findById(6);

        assertThat(byId).isEmpty();
    }

    @Test
    void deleteById_whenFilmExists() {
        boolean deleted = repository.deleteById(1L);

        assertThat(deleted).isTrue();
        assertThatThrownBy(() -> jdbc.queryForMap("SELECT * FROM FILMS WHERE FILM_ID = 1"))
                .isInstanceOf(EmptyResultDataAccessException.class);

    }

    @Test
    void deleteById_whenFilmNotExists() {
        boolean deleted = repository.deleteById(6L);
        assertThat(deleted).isFalse();
    }

    @Test
    void existById_whenTrue() {
        boolean existById = repository.existById(1);
        assertThat(existById).isTrue();
    }

    @Test
    void existById_whenFalse() {
        boolean existById = repository.existById(6);
        assertThat(existById).isFalse();
    }

    @Test
    void addLike_whenLikeIsNotSet() {
        boolean addLike = repository.addLike(1, 1);
        assertThat(addLike).isTrue();
    }

    @Test
    void addLike_whenLikeIsSet() {
        addLike(1, 1);

        boolean addLike = repository.addLike(1, 1);
        assertThat(addLike).isFalse();
    }

    @Test
    void removeLike_whenLikeIsSet() {
        addLike(1, 1);

        boolean removeLike = repository.removeLike(1, 1);
        assertThat(removeLike).isTrue();
    }

    @Test
    void removeLike_whenLikeIsNotSet() {
        boolean removeLike = repository.removeLike(1, 1);
        assertThat(removeLike).isFalse();
    }

    @Test
    void findGenresByFilmId() {
        List<Genre> genresByFilmId = repository.findGenresByFilmId(1);

        assertEquals(2, genresByFilmId.size());
        assertEquals(2, genresByFilmId.getFirst().getId());
        assertEquals("Драма", genresByFilmId.getFirst().getName());
        assertEquals(6, genresByFilmId.getLast().getId());
        assertEquals("Боевик", genresByFilmId.getLast().getName());
    }

    @Test
    void rateByFilmId() {
        int rateBefore = repository.rateByFilmId(1);
        assertEquals(0, rateBefore);

        addLike(1, 1);

        int rateAfter = repository.rateByFilmId(1);
        assertEquals(1, rateAfter);
        repository.removeLike(1, 1);
    }

    @Test
    void updateGenres() {
        List<Integer> genresBefore =
                jdbc.queryForList("SELECT GENRE_ID FROM FILM_GENRES WHERE FILM_ID=1", Integer.class);
        assertThat(genresBefore).contains(2);
        assertThat(genresBefore).contains(6);

        repository.updateGenres(List.of(
                new Genre(2, null),
                new Genre(3, null),
                new Genre(4, null)), 1);

        List<Integer> genresAfter =
                jdbc.queryForList("SELECT GENRE_ID FROM FILM_GENRES WHERE FILM_ID=1", Integer.class);
        assertThat(genresAfter).contains(2);
        assertThat(genresAfter).contains(3);
        assertThat(genresAfter).contains(4);

    }

    @BeforeEach
    void initDb() {
        jdbc.update("INSERT INTO PUBLIC.FILMS (FILM_ID,NAME,DESCRIPTION,RELEASE_DATE,DURATION,MPA_ID)\n" +
                    "\tVALUES (1,'film','desc','2020-01-01',121,1)");
        jdbc.update("INSERT INTO PUBLIC.USERS (USER_ID,EMAIL,LOGIN,NAME,BIRTHDAY)\n" +
                    "\tVALUES (1,'email','login','name','2000-01-01')");
        jdbc.update("MERGE INTO FILM_GENRES(FILM_ID, GENRE_ID) VALUES(?,?)", 1L, 2);
        jdbc.update("MERGE INTO FILM_GENRES(FILM_ID, GENRE_ID) VALUES(?,?)", 1L, 6);
    }

    @AfterEach
    void clearTables() {
        jdbc.update("DELETE FROM FILM_GENRES");
        jdbc.update("DELETE FROM USERS");
        jdbc.update("DELETE FROM FILMS");
    }

    private void addLike(long filmId, long userId) {
        jdbc.update("INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?,?)", filmId, userId);
    }
}