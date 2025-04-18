package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
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
import ru.yandex.practicum.filmorate.utils.SearchBy;

import java.sql.*;
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

    public List<Film> findAll() {
        String findAllQuery = "SELECT * FROM FILMS";
        return jdbc.query(findAllQuery, filmRowMapper);
    }

    public Film save(Film film) {
        String insertFilmQuery = """
                INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
                VALUES (?,?,?,?,?)""";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertFilmQuery, Statement.RETURN_GENERATED_KEYS);
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
        String updateQuery = """
                UPDATE FILMS
                SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?""";
        Long id = film.getId();
        if (!existById(id)) throw new NotFoundException("There is no film with id=" + id);

        jdbc.update(updateQuery,
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
        String findFilmByIdQuery = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        try {
            Film film = jdbc.queryForObject(findFilmByIdQuery, filmRowMapper, id);
            return Optional.ofNullable(film);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean deleteById(long id) {
        String deleteById = "DELETE FROM FILMS WHERE FILM_ID = ?";
        int updatedRows = jdbc.update(deleteById, id);
        return updatedRows > 0;
    }

    public boolean existById(long id) {
        String isFilmExist = "SELECT EXISTS(SELECT 1 FROM FILMS WHERE FILM_ID = ?)";
        return jdbc.queryForObject(isFilmExist, Boolean.class, id);
    }

    public boolean addLike(long id, long userId, Double mark) {
        String addLikeRowQuery = "MERGE INTO FILM_LIKES(FILM_ID, USER_ID, MARK) VALUES (?,?,?)";
        try {
            jdbc.update(addLikeRowQuery, id, userId, mark);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean removeLike(long id, long userId) {
        String deleteLikeRowQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID=? AND USER_ID=?";
        int update = jdbc.update(deleteLikeRowQuery, id, userId);
        return update > 0;
    }

    public List<Genre> findGenresByFilmId(long id) {
        String genresByFilmIdQuery = """
                SELECT * FROM GENRES WHERE GENRE_ID IN(
                    SELECT GENRE_ID FROM FILM_GENRES WHERE FILM_ID = ?)""";
        return jdbc.query(genresByFilmIdQuery, genreRowMapper, id);
    }

    public List<Director> findDirectorsByFilmId(long id) {
        String sql = """
                SELECT *
                FROM DIRECTORS D
                JOIN FILM_DIRECTORS FD ON D.DIRECTOR_ID=FD.DIRECTOR_ID
                WHERE FD.FILM_ID=?
                ORDER BY FD.DIRECTOR_ID;""";
        return jdbc.query(sql, directorRowMapper, id);
    }

    public Double rateByFilmId(long id) {
        String calculateRateByFilmId = "SELECT AVG(MARK) FROM FILM_LIKES WHERE FILM_ID = ?";
        return jdbc.queryForObject(calculateRateByFilmId, Double.class, id);
    }

    public void updateGenres(List<Genre> genres, long id) {
        String deleteFilmsGenresRowQuery = "DELETE FROM FILM_GENRES WHERE FILM_ID=?";

        jdbc.update(deleteFilmsGenresRowQuery, id);

        if (genres == null || genres.isEmpty()) {
            return;
        }
        String insertFilmGenresQuery = "MERGE INTO FILM_GENRES(FILM_ID, GENRE_ID) VALUES(?,?)";
        for (Genre genre : genres) {
            jdbc.update(insertFilmGenresQuery, id, genre.getId());
        }
    }

    public List<Film> findFilmByUserIdLike(long userId) {
        String findByUserIdLikes = """
                SELECT * FROM FILMS
                WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_LIKES WHERE USER_ID = ?)""";
        return jdbc.query(findByUserIdLikes, filmRowMapper, userId);
    }

    public List<Film> getPopularFilmsByGenreAndYear(int genreId, int year, int count) {
        String sql = """
                SELECT *
                FROM FILMS WHERE FILM_ID IN (
                SELECT FILM_ID FROM FILM_GENRES WHERE GENRE_ID = ?
                INTERSECT
                SELECT FILM_ID FROM FILMS WHERE YEAR(FILMS.RELEASE_DATE) = ?)
                LIMIT ?""";
        return jdbc.query(sql, filmRowMapper, genreId, year, count);
    }

    public List<Film> getPopularFilmsByYear(int year, int count) {
        String sql = """
                SELECT *
                FROM FILMS WHERE FILM_ID IN (
                SELECT FILM_ID FROM FILMS WHERE YEAR(FILMS.RELEASE_DATE) = ?)
                LIMIT ?""";
        return jdbc.query(sql, filmRowMapper, year, count);
    }

    public List<Film> getPopularFilmsByGenre(int genreId, int count) {
        String sql = """
                SELECT *
                FROM FILMS WHERE FILM_ID IN (
                SELECT FILM_ID FROM FILM_GENRES WHERE GENRE_ID = ?)
                LIMIT ?""";
        return jdbc.query(sql, filmRowMapper, genreId, count);
    }

    public List<Film> search(String queryForSearch, SearchBy searchBy) {
        return jdbc.query(searchBy.getQuery(), filmRowMapper, "%" + queryForSearch + "%");
    }

    public List<Director> updateDirectors(List<Director> directors, long id) {
        String sql = """
                DELETE FROM FILM_DIRECTORS
                WHERE FILM_ID=?""";
        jdbc.update(sql, id);
        List<Director> directorsWithName = new ArrayList<>();

        if (directors == null || directors.isEmpty()) {
            return directorsWithName;
        }
        sql = "MERGE INTO FILM_DIRECTORS(FILM_ID, DIRECTOR_ID) VALUES(?,?)";
        String sqlAddNames = """
                SELECT *
                FROM DIRECTORS d
                JOIN FILM_DIRECTORS fd ON d.DIRECTOR_ID=fd.DIRECTOR_ID
                WHERE fd.FILM_ID=? AND d.DIRECTOR_ID=?""";
        for (Director director : directors) {
            jdbc.update(sql, id, director.getId());
            directorsWithName.add(jdbc.queryForObject(sqlAddNames, directorRowMapper, id, director.getId()));
        }
        return directorsWithName;
    }

    public List<Film> getFilmsByDirectorId(int directorId) {
        String query = """
                SELECT * FROM FILMS WHERE FILM_ID IN(
                    SELECT FILM_ID FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?)""";
        return jdbc.query(query, filmRowMapper, directorId);
    }

    public List<Film> getRecommendations(long userId) {
        String bestMatchUserIdsQuery = """
                SELECT USER_ID
                FROM FILM_LIKES
                WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_LIKES WHERE USER_ID = ?) AND NOT USER_ID = ?
                GROUP BY USER_ID
                ORDER BY COUNT(USER_ID) DESC""";
        String recommendationsQuery = """
                SELECT * FROM FILMS
                WHERE FILM_ID IN(
                	SELECT FILM_ID
                	FROM FILM_LIKES WHERE USER_ID = ?
                	EXCEPT
                	SELECT FILM_ID
                	FROM FILM_LIKES WHERE USER_ID = ?)""";
        List<Film> result = new ArrayList<>();
        jdbc.query(bestMatchUserIdsQuery, new ResultSetExtractor<Void>() {
            @Override
            public Void extractData(ResultSet rs) throws SQLException, DataAccessException {
                while (rs.next()) {
                    long matchUserId = rs.getLong("USER_ID");
                    List<Film> recommendations = jdbc.query(recommendationsQuery, filmRowMapper, matchUserId, userId);
                    if (!recommendations.isEmpty()) {
                        result.addAll(recommendations);
                        return null;
                    }
                }
                return null;
            }
        }, userId, userId);
        return result;
    }
}
