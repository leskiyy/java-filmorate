package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }


    public Film updateFilm(@Valid Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film addFilm(@Valid Film film) {
        return filmStorage.addFilm(film);
    }

    public boolean addFilmLike(@Positive long id, @Positive long userId) {
        validateUserAndFilm(id, userId);
        return filmStorage.addLike(id, userId);
    }

    public boolean deleteFilmLike(@Positive long id, @Positive long userId) {
        validateUserAndFilm(id, userId);
        return filmStorage.removeLike(id, userId);
    }

    public List<Film> getPopularFilms(@Positive int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .limit(count)
                .toList();
    }

    private void validateUserAndFilm(long id, long userId) {
        Optional<User> userById = userStorage.getUserById(userId);
        if (userById.isEmpty()) {
            throw new NotFoundException("There is no user with id=" + userId);
        }
        Optional<Film> filmById = filmStorage.getFilmById(id);
        if (filmById.isEmpty()) {
            throw new NotFoundException("There is no film with id=" + id);
        }
    }


}
