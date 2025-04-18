package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.utils.FilmMapper;
import ru.yandex.practicum.filmorate.utils.OperationType;
import ru.yandex.practicum.filmorate.utils.SearchBy;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final MpaRepository mpaRepository;
    private final EventService eventService;
    private final ValidationService validationService;

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

    public FilmDTO addFilm(FilmDTO film) {
        validationService.validateFilmDto(film);

        Film save = filmRepository.save(FilmMapper.mapToFilm(film));

        filmRepository.updateGenres(film.getGenres(), save.getId());
        film.setDirectors(filmRepository.updateDirectors(film.getDirectors(), save.getId()));
        film.setId(save.getId());
        return getFilmById(save.getId());
    }

    public boolean addFilmLike(long id, long userId, Double mark) {
        validationService.validateFilmById(id);
        validationService.validateUserById(userId);
        if (mark != null) validationService.validateMark(mark);
        eventService.createLikeEvent(userId, id, OperationType.ADD);
        return filmRepository.addLike(id, userId, mark);
    }

    public boolean deleteFilmLike(long id, long userId) {
        validationService.validateFilmById(id);
        validationService.validateUserById(userId);
        eventService.createLikeEvent(userId, id, OperationType.REMOVE);
        return filmRepository.removeLike(id, userId);
    }

    public List<FilmDTO> getPopularFilms(Integer genreId, Integer year, @Positive int count) {
        List<Film> films;
        if (genreId != null && year != null) {
            films = filmRepository.getPopularFilmsByGenreAndYear(genreId, year, count);
        } else if (genreId != null) {
            films = filmRepository.getPopularFilmsByGenre(genreId, count);
        } else if (year != null) {
            films = filmRepository.getPopularFilmsByYear(year, count);
        } else {
            films = filmRepository.findAll();
        }

        return films.stream()
                .map(this::toDTO)
                .sorted(Comparator.comparing(FilmDTO::getRate, Comparator.nullsLast(Comparator.reverseOrder())))
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

    public List<FilmDTO> searchFilms(String query, String... searchOptions) {
        Set<Film> filmSet = new HashSet<>();
        for (String by : searchOptions) {
            try {
                SearchBy searchBy = SearchBy.valueOf(by.toUpperCase());
                filmSet.addAll(filmRepository.search(query, searchBy));
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Search options must be in " + Arrays.toString(SearchBy.values()));
            }
        }
        return filmSet.stream().map(this::toDTO)
                .sorted(Comparator.comparing(FilmDTO::getRate, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public List<FilmDTO> getSortedByDirectorFilms(int directorId, String sortBy) {
        validationService.validateDirectorById(directorId);
        List<Film> filmsByDirectorId = filmRepository.getFilmsByDirectorId(directorId);
        Comparator<FilmDTO> comparator = null;
        if (sortBy.equals("rate")) {
            comparator = Comparator.comparing(FilmDTO::getRate, Comparator.nullsLast(Comparator.reverseOrder()));
        } else if (sortBy.equals("year")) {
            comparator = Comparator.comparing(FilmDTO::getReleaseDate);
        } else {
            throw new ValidationException("Sort by must be rate or year");
        }
        return filmsByDirectorId.stream()
                .map(this::toDTO)
                .sorted(comparator)
                .toList();
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

    private FilmDTO toDTO(Film film) {
        return FilmMapper.mapToDto(
                film,
                filmRepository.findGenresByFilmId(film.getId()),
                mpaRepository.findById(film.getMpa()).orElse(null),
                filmRepository.rateByFilmId(film.getId()),
                filmRepository.findDirectorsByFilmId(film.getId()));
    }
}
