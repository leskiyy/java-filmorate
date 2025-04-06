package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreRepository {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM GENRES";
    private static final String IS_EXIST_QUERY = "SELECT EXISTS(SELECT 1 FROM GENRES WHERE GENRE_ID = ?)";

    public Optional<Genre> findById(int id) {
        try {
            Genre genre = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(genre);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Genre> findAll() {
        List<Genre> query = jdbc.query(FIND_ALL_QUERY, mapper);
        query.sort(Comparator.comparingInt(Genre::getId));
        return query;
    }

    public boolean existById(int id) {
        return jdbc.queryForObject(IS_EXIST_QUERY, Boolean.class, id);
    }

}

