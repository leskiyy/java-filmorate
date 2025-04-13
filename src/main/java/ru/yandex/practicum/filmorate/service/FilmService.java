package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.utils.FilmMapper;
import ru.yandex.practicum.filmorate.utils.SearchBy;

import java.util.*;

@Service
@Validated
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final EventService eventService;
    private final ValidationService validationService;
    private static final String METHOD_ADD = "ADD";
    private static final String METHOD_REMOVE = "REMOVE";
    private static final String METHOD_UPDATE = "UPDATE";

    public List<FilmDTO> getAllFilms() {
        return filmRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public FilmDTO updateFilm(FilmDTO filmDto) {
        validationService.validateFilmById(filmDto.getId());
        validationService.validateFilmDto(filmDto);

        filmRepository.update(FilmMapper.mapToFilm(filmDto));

        filmRepository.updateGenres(filmDto.getGenres(), filmDto.getId());
        filmDto.setDirectors(filmRepository.updateDirectors(filmDto.getDirectors(), filmDto.getId()));

        return getFilmById(filmDto.getId());
    }

    public FilmDTO addFilm(@Valid FilmDTO film) {
        validationService.validateFilmDto(film);

        Film save = filmRepository.save(FilmMapper.mapToFilm(film));

        filmRepository.updateGenres(film.getGenres(), save.getId());
        film.setDirectors(filmRepository.updateDirectors(film.getDirectors(), save.getId()));
        film.setId(save.getId());
        return getFilmById(save.getId());
    }

    public boolean addFilmLike(long id, long userId) {
        validationService.validateFilmById(id);
        validationService.validateUserById(userId);
        eventService.createLikeEvent(userId, id, METHOD_ADD);
        return filmRepository.addLike(id, userId);
    }

    public boolean deleteFilmLike(long id, long userId) {
        validationService.validateFilmById(id);
        validationService.validateUserById(userId);
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
        return toDTO(optFilm.get());
    }

    public List<FilmDTO> getCommonFilms(long userId, long friendId) {
        validationService.validateUserById(userId, friendId);
        List<Film> userFilms = filmRepository.findFilmByUserIdLike(userId);
        List<Long> friendFilmsIds = filmRepository.findFilmByUserIdLike(friendId).stream().map(Film::getId).toList();
        return userFilms.stream()
                .filter(film -> friendFilmsIds.contains(film.getId()))
                .map(this::toDTO)
                .toList();
    }

    public List<FilmDTO> getPopularFilmsByGenreAndYear(long genreId, int year, int count) {
        List<Film> films = filmRepository.getPopularFilmsByGenreAndYear(genreId, year, count);
        return films.stream()
                .map(this::toDTO)
                .toList();
    }

    public List<FilmDTO> getPopularFilmsByGenre(long genreId, int count) {
        List<Film> films = filmRepository.getPopularFilmsByGenre(genreId, count);
        return films.stream()
                .map(this::toDTO)
                .toList();
    }

    public List<FilmDTO> getPopularFilmsByYear(Integer year, int count) {
        List<Film> films = filmRepository.getPopularFilmsByYear(year, count);
        return films.stream()
                .map(this::toDTO)
                .toList();
    }

    public List<FilmDTO> searchFilms(String query, String... searchOptions) {
        Set<Film> filmSet = new HashSet<>();
        for (String by : searchOptions) {
            try {
                SearchBy searchBy = SearchBy.valueOf(by.toUpperCase());
                filmSet.addAll(filmRepository.search(query, searchBy));
            } catch (IllegalArgumentException e) {
                // Illegal search param skip
            }
        }
        return filmSet.stream().map(this::toDTO)
                .sorted(Comparator.comparing(FilmDTO::getRate).reversed().thenComparingLong(FilmDTO::getId))
                .toList();
    }

    public List<FilmDTO> getSortedByDirectorFilms(int directorId, String sortBy) {
        validationService.validateDirectorById(directorId);
        List<Long> ids = new ArrayList<>();
        if (sortBy.equals("year")) {
            ids = filmRepository.sortedByYear(directorId);
        } else if (sortBy.equals("likes")) {
            ids = filmRepository.sortedByLikes(directorId);
        }
        List<FilmDTO> sortedFilms = new ArrayList<>();
        for (Long filmId : ids) {
            sortedFilms.add(getFilmById(filmId));
        }
        return sortedFilms;
    }

    private FilmDTO toDTO(Film film) {
        return FilmMapper.mapToDto(
                film,
                filmRepository.findGenresByFilmId(film.getId()),
                mpaRepository.findById(film.getMpa()).orElse(null),
                filmRepository.rateByFilmId(film.getId()),
                filmRepository.findDirectorsByFilmId(film.getId()));
    }

    public List<FilmDTO> getRecommendations(long userId) {
        validationService.validateUserById(userId);
        List<Film> recommendations = filmRepository.getRecommendations(userId);
        return recommendations.stream()
                .map(this::toDTO)
                .toList();
    }

    public boolean deleteFilmById(long filmId) {
        return filmRepository.deleteById(filmId);
    }
}
