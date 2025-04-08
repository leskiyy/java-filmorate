package ru.yandex.practicum.filmorate.utils;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@UtilityClass
public class FilmMapper {
    public Film mapToFilm(FilmDTO dto) {
        Integer mpaId = dto.getMpa() == null ? null : dto.getMpa().getId();

        return new Film().setId(dto.getId())
                .setName(dto.getName())
                .setDescription(dto.getDescription())
                .setReleaseDate(dto.getReleaseDate())
                .setDuration(dto.getDuration())
                .setMpa(mpaId);
    }

    public FilmDTO mapToDto(Film film, List<Genre> genres, Mpa mpa, int rate, List<Director> directors) {
        return new FilmDTO()
                .setId(film.getId())
                .setName(film.getName())
                .setDescription(film.getDescription())
                .setReleaseDate(film.getReleaseDate())
                .setDuration(film.getDuration())
                .setRate(rate)
                .setMpa(mpa)
                .setGenres(genres)
                .setDirectors(directors);
    }
}
