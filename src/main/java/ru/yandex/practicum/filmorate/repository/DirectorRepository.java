package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DirectorRepository {
    private final JdbcTemplate jdbc;
    private final DirectorRowMapper mapper;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM DIRECTORS";
    private static final String ADD_DIRECTOR_QUERY = "INSERT INTO DIRECTORS (NAME) VALUES (?)";
    private static final String ADD_FILM_DIRECTOR_QUERY = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE DIRECTORS SET NAME=? WHERE DIRECTOR_ID=?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID=?";
    private static final String IS_EXIST_QUERY = "SELECT COUNT(*) FROM DIRECTORS WHERE DIRECTOR_ID=?";

    public Optional<Director> findById(int id) {
        if (!isDirectorExists(id)){
            throw new NotFoundException(String.format("Director with ID %d not found", id));
        }
        try {
            Director director = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(director);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Director> findAll() {
        List<Director> query = jdbc.query(FIND_ALL_QUERY, mapper);
        query.sort(Comparator.comparingInt(Director::getId));
        return query;
    }

    public Director addDirector(Director director) {
        jdbc.update(ADD_DIRECTOR_QUERY, director.getName());
        return jdbc.queryForObject("SELECT * FROM DIRECTORS ORDER BY director_id DESC LIMIT 1;", mapper);
    }

    public void addFilmDirector(Director director, Film film) {
        jdbc.update(ADD_FILM_DIRECTOR_QUERY, film.getId(), director.getId());
        //return jdbc.queryForObject("SELECT * FROM FILMS ORDER BY FILM_ID DESC LIMIT 1;", mapper);
    }

    public Director updateDirector(Director director) {
        if (!isDirectorExists(director.getId())){
            throw new NotFoundException(String.format("Director with ID %d not found", director.getId()));
        }
        jdbc.update(UPDATE_DIRECTOR_QUERY, director.getName(), director.getId());
        return jdbc.queryForObject("SELECT * FROM DIRECTORS ORDER BY director_id DESC LIMIT 1;", mapper);
    }

    public void deleteDirector(int id) {
        if (!isDirectorExists(id)){
            throw new NotFoundException(String.format("Director with ID %d not found", id));
        }
        jdbc.update(DELETE_DIRECTOR_QUERY, id);
    }

    public boolean isDirectorExists(int id) {
        return jdbc.queryForObject(IS_EXIST_QUERY, Integer.class, id) > 0;
    }

}
