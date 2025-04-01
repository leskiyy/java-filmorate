package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class MpaRepository implements MpaStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM MPA WHERE MPA_ID = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM MPA";
    private static final String IS_EXIST_QUERY = "SELECT EXISTS(SELECT 1 FROM MPA WHERE MPA_ID = ?)";

    @Override
    public Optional<Mpa> findById(int id) {
        try {
            Mpa mpa = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(mpa);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> findAll() {
        List<Mpa> query = jdbc.query(FIND_ALL_QUERY, mapper);
        query.sort(Comparator.comparingInt(Mpa::getId));
        return query;
    }

    @Override
    public boolean existById(int id) {
        return jdbc.queryForObject(IS_EXIST_QUERY, Boolean.class, id);
    }
}

