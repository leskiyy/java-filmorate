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

    public Optional<Director> findById(int id) {
        if (!isDirectorExists(id)) {
            throw new NotFoundException(String.format("Director with ID %d not found", id));
        }
        try {
            String findByIdQuery = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
            Director director = jdbc.queryForObject(findByIdQuery, mapper, id);
            return Optional.ofNullable(director);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Director> findAll() {
        String findAllQuery = "SELECT * FROM DIRECTORS";
        List<Director> query = jdbc.query(findAllQuery, mapper);
        query.sort(Comparator.comparingInt(Director::getId));
        return query;
    }

    public Director addDirector(Director director) {
        String addDirectorQuery = "INSERT INTO DIRECTORS (NAME) VALUES (?)";
        jdbc.update(addDirectorQuery, director.getName());
        return jdbc.queryForObject("SELECT * FROM DIRECTORS ORDER BY director_id DESC LIMIT 1;", mapper);
    }

    public void addFilmDirector(Director director, Film film) {
        String addFilmDirectorQuery = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
        jdbc.update(addFilmDirectorQuery, film.getId(), director.getId());
    }

    public Director updateDirector(Director director) {
        if (!isDirectorExists(director.getId())) {
            throw new NotFoundException(String.format("Director with ID %d not found", director.getId()));
        }
        String updateDirectorQuery = "UPDATE DIRECTORS SET NAME=? WHERE DIRECTOR_ID=?";
        jdbc.update(updateDirectorQuery, director.getName(), director.getId());
        return jdbc.queryForObject("SELECT * FROM DIRECTORS WHERE DIRECTOR_ID=?;", mapper, director.getId());
    }

    public void deleteDirector(int id) {
        if (!isDirectorExists(id)) {
            throw new NotFoundException(String.format("Director with ID %d not found", id));
        }
        String deleteDirectorQuery = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID=?";
        jdbc.update(deleteDirectorQuery, id);
    }

    public boolean isDirectorExists(int id) {
        String isExistQuery = "SELECT COUNT(*) FROM DIRECTORS WHERE DIRECTOR_ID=?";
        return jdbc.queryForObject(isExistQuery, Integer.class, id) > 0;
    }

}
