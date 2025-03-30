package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.FilmMapper;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public List<FilmDTO> getAllFilms() {
        return filmStorage.findAll().stream()
                .map(film -> {
                    Mpa mpa = film.getMpa() == null ? null : mpaStorage.findById(film.getMpa()).orElse(null);
                    return FilmMapper.mapToDto(film,
                            filmStorage.findGenresByFilmId(film.getId()),
                            mpa,
                            filmStorage.rateByFilmId(film.getId()));
                })
                .toList();
    }

    public FilmDTO updateFilm(@Valid FilmDTO filmDto) {
        if (!filmStorage.existById(filmDto.getId())) {
            throw new NotFoundException("There is no film with id=" + filmDto.getId());
        }
        validateFilm(filmDto);

        filmStorage.update(FilmMapper.mapToFilm(filmDto));

        if (filmDto.getGenres() != null) {
            filmStorage.updateGenres(filmDto.getGenres(), filmDto.getId());
        }
        int rate = filmStorage.rateByFilmId(filmDto.getId());

        return filmDto.setRate(rate);
    }

    public FilmDTO addFilm(@Valid FilmDTO film) {
        validateFilm(film);
        Film save = filmStorage.save(FilmMapper.mapToFilm(film));
        filmStorage.updateGenres(film.getGenres(), save.getId());
        film.setId(save.getId());
        return film;
    }

    public boolean addFilmLike(@Positive long id, @Positive long userId) {
        validateUserAndFilm(id, userId);
        return filmStorage.addLike(id, userId);
    }

    public boolean deleteFilmLike(@Positive long id, @Positive long userId) {
        validateUserAndFilm(id, userId);
        return filmStorage.removeLike(id, userId);
    }

    public List<FilmDTO> getPopularFilms(@Positive int count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparingInt(FilmDTO::getRate).reversed())
                .limit(count)
                .toList();
    }

    public FilmDTO getFilmById(@Positive long id) {
        Optional<Film> optFilm = filmStorage.findById(id);
        if (optFilm.isEmpty()) {
            throw new NotFoundException("There is no film with id=" + id);
        }
        Film film = optFilm.get();
        Integer mpaId = film.getMpa();
        Mpa mpa = mpaId == null ? null : mpaStorage.findById(film.getMpa()).orElse(null);
        List<Genre> genresByFilmId = filmStorage.findGenresByFilmId(id);
        int rate = filmStorage.rateByFilmId(id);
        return FilmMapper.mapToDto(film, genresByFilmId, mpa, rate);
    }

    private void validateUserAndFilm(long id, long userId) {
        if (!userStorage.existById(userId)) {
            throw new NotFoundException("There is no user with id=" + userId);
        }
        if (!filmStorage.existById(id)) {
            throw new NotFoundException("There is no film with id=" + id);
        }
    }


    private void validateFilm(FilmDTO film) {
        if (film.getMpa() != null && !mpaStorage.existById(film.getMpa().getId())) {
            throw new NotFoundException("There is no mpa with id=" + film.getMpa().getId());
        }
        List<Genre> genres = film.getGenres();

        if (genres == null) {
            return;
        }
        for (Genre genre : genres) {
            if (!genreStorage.existById(genre.getId())) {
                throw new NotFoundException("There is no genre with id=" + genre.getId());
            }
        }
    }
}
