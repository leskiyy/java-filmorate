package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    //private final DirectorService directorService;
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Director director = new Director();
        return new Film()
                .setId(rs.getLong("FILM_ID"))
                .setName(rs.getString("NAME"))
                .setDescription(rs.getString("DESCRIPTION"))
                .setReleaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .setDuration(rs.getInt("DURATION"))
                .setMpa(rs.getInt("MPA_ID"));
    }
}
