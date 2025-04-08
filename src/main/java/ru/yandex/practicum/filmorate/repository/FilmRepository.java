package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FilmRepository {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;

    private static final String FIND_ALL_QUERY = "SELECT * FROM FILMS";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM FILMS WHERE FILM_ID = ?";
    private static final String GENRES_BY_FILM_ID_QUERY = """
            SELECT * FROM GENRES WHERE GENRE_ID IN(SELECT GENRE_ID FROM FILM_GENRES WHERE FILM_ID = ?)""";
    private static final String INSERT_FILM_QUERY = """
            INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) VALUES (?,?,?,?,?)""";
    private static final String UPDATE_FILM_QUERY = """
            UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?""";
    private static final String INSERT_FILM_GENRES_QUERY = "MERGE INTO FILM_GENRES(FILM_ID, GENRE_ID) VALUES(?,?)";
    private static final String CALCULATE_RATE_BY_FILM_ID = "SELECT COUNT(USER_ID) FROM FILM_LIKES WHERE FILM_ID = ?";
    private static final String IS_FILM_EXIST = "SELECT EXISTS(SELECT 1 FROM FILMS WHERE FILM_ID = ?)";
    private static final String DELETE_FILMS_GENRES_ROW_QUERY = "DELETE FROM FILM_GENRES WHERE FILM_ID=?";
    private static final String DELETE_LIKE_ROW_QUERY = "DELETE FROM FILM_LIKES WHERE FILM_ID=? AND USER_ID=?";
    private static final String ADD_LIKE_ROW_QUERY = "INSERT INTO FILM_LIKES(FILM_ID, USER_ID) VALUES (?,?)";
    private static final String DELETE_BY_ID = "DELETE FROM FILMS WHERE FILM_ID = ?";
    private static final String FIND_BY_USER_ID_LIKES = """
            SELECT * FROM FILMS WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_LIKES WHERE USER_ID = ?)""";


    public List<Film> findAll() {
        return jdbc.query(FIND_ALL_QUERY, filmRowMapper);
    }

    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_FILM_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return film;
    }

    public Film update(Film film) {
        Long id = film.getId();
        if (!existById(id)) throw new NotFoundException("There is no film with id=" + id);

        jdbc.update(UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa(),
                id
        );

        return film;
    }

    public Optional<Film> findById(long id) {
        try {
            Film film = jdbc.queryForObject(FIND_FILM_BY_ID_QUERY, filmRowMapper, id);
            return Optional.ofNullable(film);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean deleteById(long id) {
        int updatedRows = jdbc.update(DELETE_BY_ID, id);
        return updatedRows > 0;
    }

    public boolean existById(long id) {
        return jdbc.queryForObject(IS_FILM_EXIST, Boolean.class, id);
    }

    public boolean addLike(long id, long userId) {
        try {
            jdbc.update(ADD_LIKE_ROW_QUERY, id, userId);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean removeLike(long id, long userId) {
        int update = jdbc.update(DELETE_LIKE_ROW_QUERY, id, userId);
        return update > 0;
    }

    public List<Genre> findGenresByFilmId(long id) {
        return jdbc.query(GENRES_BY_FILM_ID_QUERY, genreRowMapper, id);
    }

    public int rateByFilmId(long id) {
        return jdbc.queryForObject(CALCULATE_RATE_BY_FILM_ID, Integer.class, id);
    }

    public void updateGenres(List<Genre> genres, long id) {
        jdbc.update(DELETE_FILMS_GENRES_ROW_QUERY, id);

        if (genres == null || genres.isEmpty()) {
            return;
        }

        for (Genre genre : genres) {
            jdbc.update(INSERT_FILM_GENRES_QUERY, id, genre.getId());
        }
    }

    public List<Film> findFilmByUserIdLike(long userId) {
        return jdbc.query(FIND_BY_USER_ID_LIKES, filmRowMapper, userId);
    }
}
