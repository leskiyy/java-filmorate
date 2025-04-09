package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FilmRepository {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;
    private final DirectorRowMapper directorRowMapper;

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

    public List<Director> findDirectorsByFilmId(long id) {
        String sql = "SELECT * " +
                    "FROM DIRECTORS D " +
                    "JOIN FILM_DIRECTORS FD ON D.DIRECTOR_ID=FD.DIRECTOR_ID " +
                    "WHERE FD.FILM_ID=? " +
                    "ORDER BY FD.DIRECTOR_ID;";
        return jdbc.query(sql, directorRowMapper, id);
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

    public List<Film> getPopularFilmsByGenreAndYear(long genreId, int year, int count) {
        String sql = "SELECT f.*, m.NAME AS MPA_NAME " +
                "FROM FILMS f " +
                "JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
                "JOIN FILM_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN FILM_LIKES fl ON f.FILM_ID = fl.FILM_ID " +
                "WHERE fg.GENRE_ID = ? AND YEAR(f.RELEASE_DATE) = ? " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(fl.USER_ID) DESC " +
                "LIMIT ?";
        return jdbc.query(sql, filmRowMapper, genreId, year, count);
    }

    public List<Film> getPopularFilmsByYear(int year, int count) {
        String sql = "SELECT f.*, m.NAME AS MPA_NAME " +
                "FROM FILMS f " +
                "JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILM_LIKES fl ON f.FILM_ID = fl.FILM_ID " +
                "WHERE YEAR(f.RELEASE_DATE) = ? " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(fl.USER_ID) DESC " +
                "LIMIT ?";
        return jdbc.query(sql, filmRowMapper, year, count);
    }

    public List<Film> getPopularFilmsByGenre(long genreId, int count) {
        String sql = "SELECT f.*, m.NAME AS MPA_NAME " +
                "FROM FILMS f " +
                "JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
                "JOIN FILM_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN FILM_LIKES fl ON f.FILM_ID = fl.FILM_ID " +
                "WHERE fg.GENRE_ID = ? " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(fl.USER_ID) DESC " +
                "LIMIT ?";
        return jdbc.query(sql, filmRowMapper, genreId, count);
    }
    public List<Director> updateDirectors(List<Director> directors, long id) {
        String sql = "DELETE FROM FILM_DIRECTORS " +
                    "WHERE FILM_ID=?";
        jdbc.update(sql, id);
        List<Director> directorsWithName = new ArrayList<>();

        if (directors == null || directors.isEmpty()) {
            return directorsWithName;
        }
        sql = "MERGE INTO FILM_DIRECTORS(FILM_ID, DIRECTOR_ID) VALUES(?,?)";
        String sqlAddNames = "SELECT * " +
                            "FROM DIRECTORS d " +
                            "JOIN FILM_DIRECTORS fd ON d.DIRECTOR_ID=fd.DIRECTOR_ID " +
                            "WHERE fd.FILM_ID=? AND d.DIRECTOR_ID=?;";
        for (Director director : directors) {
            jdbc.update(sql, id, director.getId());
            directorsWithName.add(jdbc.queryForObject(sqlAddNames, directorRowMapper, id, director.getId()));
        }
        return directorsWithName;
    }

    public List<Long> sortedByYear(int directorId) {
        String sql = "SELECT f.FILM_ID " +
                    "FROM FILMS f " +
                    "JOIN FILM_DIRECTORS fd ON f.film_id = fd.film_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY f.release_date;";
        return jdbc.query(sql, (rs, rowNum) -> rs.getLong("FILM_ID"), directorId);
    }

    public List<Long> sortedByLikes(int directorId) {
        String sql = "SELECT f.film_id, " +
                            "COUNT(l.film_id) as likes_count " +
                    "FROM FILMS f " +
                    "JOIN FILM_DIRECTORS fd ON f.film_id = fd.film_id " +
                    "LEFT JOIN FILM_LIKES l ON f.film_id = l.film_id " +
                    "WHERE fd.director_id = ? GROUP BY f.film_id " +
                    "ORDER BY likes_count DESC;";
        return jdbc.query(sql, (rs, rowNum) -> rs.getLong("FILM_ID"), directorId);
    }

}
