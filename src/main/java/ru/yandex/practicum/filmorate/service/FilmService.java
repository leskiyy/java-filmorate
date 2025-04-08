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
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.utils.FilmMapper;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;
    private final EventService eventService;
    private static final String METHOD_ADD = "ADD";
    private static final String METHOD_REMOVE = "REMOVE";
    private static final String METHOD_UPDATE = "UPDATE";

    public List<FilmDTO> getAllFilms() {
        return filmRepository.findAll().stream()
                .map(film -> {
                    Mpa mpa = film.getMpa() == null ? null : mpaRepository.findById(film.getMpa()).orElse(null);
                    return FilmMapper.mapToDto(film,
                            filmRepository.findGenresByFilmId(film.getId()),
                            mpa,
                            filmRepository.rateByFilmId(film.getId()));
                })
                .toList();
    }

    public FilmDTO updateFilm(@Valid FilmDTO filmDto) {
        if (!filmRepository.existById(filmDto.getId())) {
            throw new NotFoundException("There is no film with id=" + filmDto.getId());
        }
        validateFilm(filmDto);

        filmRepository.update(FilmMapper.mapToFilm(filmDto));

        if (filmDto.getGenres() != null) {
            filmRepository.updateGenres(filmDto.getGenres(), filmDto.getId());
        }
        int rate = filmRepository.rateByFilmId(filmDto.getId());
        return filmDto.setRate(rate);
    }

    public FilmDTO addFilm(@Valid FilmDTO film) {
        validateFilm(film);
        Film save = filmRepository.save(FilmMapper.mapToFilm(film));
        filmRepository.updateGenres(film.getGenres(), save.getId());
        film.setId(save.getId());
        return film;
    }

    public boolean addFilmLike(@Positive long id, @Positive long userId) {
        validateUserAndFilm(id, userId);
        eventService.createLikeEvent(userId, id, METHOD_ADD);
        return filmRepository.addLike(id, userId);
    }

    public boolean deleteFilmLike(@Positive long id, @Positive long userId) {
        validateUserAndFilm(id, userId);
        eventService.createLikeEvent(userId, id, METHOD_REMOVE);
        return filmRepository.removeLike(id, userId);
    }

    public List<FilmDTO> getPopularFilms(@Positive int count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparingInt(FilmDTO::getRate).reversed())
                .limit(count)
                .toList();
    }

    public FilmDTO getFilmById(@Positive long id) {
        Optional<Film> optFilm = filmRepository.findById(id);
        if (optFilm.isEmpty()) {
            throw new NotFoundException("There is no film with id=" + id);
        }
        Film film = optFilm.get();
        Integer mpaId = film.getMpa();
        Mpa mpa = mpaId == null ? null : mpaRepository.findById(film.getMpa()).orElse(null);
        List<Genre> genresByFilmId = filmRepository.findGenresByFilmId(id);
        int rate = filmRepository.rateByFilmId(id);
        return FilmMapper.mapToDto(film, genresByFilmId, mpa, rate);
    }

    private void validateUserAndFilm(long id, long userId) {
        if (!userRepository.existById(userId)) {
            throw new NotFoundException("There is no user with id=" + userId);
        }
        if (!filmRepository.existById(id)) {
            throw new NotFoundException("There is no film with id=" + id);
        }
    }

    private void validateFilm(FilmDTO film) {
        if (film.getMpa() != null && !mpaRepository.existById(film.getMpa().getId())) {
            throw new NotFoundException("There is no mpa with id=" + film.getMpa().getId());
        }
        List<Genre> genres = film.getGenres();

        if (genres == null) {
            return;
        }
        for (Genre genre : genres) {
            if (!genreRepository.existById(genre.getId())) {
                throw new NotFoundException("There is no genre with id=" + genre.getId());
            }
        }
    }
}
