package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;

    public FilmController() {
        this.filmStorage = new FilmStorage();
    }

    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        log.info("Successfully get films");
        return films;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        try {
            Film updatedFilm = filmStorage.updateFilm(film);
            log.info("Successfully update film {}", updatedFilm);
            return updatedFilm;
        } catch (NotFoundException e) {
            log.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        Film addedFilm = filmStorage.addFilm(film);
        log.info("Successfully add film {}", addedFilm);
        return addedFilm;
    }
}
