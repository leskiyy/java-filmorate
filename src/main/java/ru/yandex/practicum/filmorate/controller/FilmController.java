package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<FilmDTO> getAllFilms() {
        List<FilmDTO> films = filmService.getAllFilms();
        log.info("Successfully get films");
        return films;
    }

    @GetMapping("/{id}")
    public FilmDTO getFilmById(@PathVariable long id) {
        FilmDTO film = filmService.getFilmById(id);
        log.info("Successfully get film id={}", film.getId());
        return film;
    }

    @PutMapping
    public FilmDTO updateFilm(@RequestBody FilmDTO film) {
        FilmDTO updatedFilm = filmService.updateFilm(film);
        log.info("Successfully update film id={}", updatedFilm.getId());
        return updatedFilm;
    }

    @PostMapping
    public FilmDTO addFilm(@Valid @RequestBody FilmDTO film) {
        FilmDTO addedFilm = filmService.addFilm(film);
        log.info("Successfully add film id={}", addedFilm.getId());
        return addedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addFilmLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addFilmLike(id, userId, null);
    }

    @PutMapping("/{id}/like/{userId}/{mark}")
    public void addFilmLike(@PathVariable long id, @PathVariable long userId, @PathVariable double mark) {
        filmService.addFilmLike(id, userId, mark);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteFilmLike(@PathVariable long id, @PathVariable long userId) {
        filmService.deleteFilmLike(id, userId);
    }

    @GetMapping("/common")
    public List<FilmDTO> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        List<FilmDTO> commonFilms = filmService.getCommonFilms(userId, friendId);
        log.info("Successfully get common {} films", commonFilms.size());
        return commonFilms;
    }

    @GetMapping("/popular")
    public List<FilmDTO> getPopularFilmsByGenreAndYear(@RequestParam(required = false) Integer genreId,
                                                       @RequestParam(required = false) Integer year,
                                                       @RequestParam(defaultValue = "10", required = false) int count) {
        return filmService.getPopularFilms(genreId, year, count);
    }

    @GetMapping("/search")
    public List<FilmDTO> search(@RequestParam String query, @RequestParam String by) {
        List<FilmDTO> search = filmService.searchFilms(query, by.split(","));
        log.info("Found {} films: query {} by {} ", search.size(), query, by);
        return search;
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDTO> getSortedByDirectorFilms(@PathVariable int directorId, @RequestParam(defaultValue = "year") String sortBy) {
        return filmService.getSortedByDirectorFilms(directorId, sortBy);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable long filmId) {
        filmService.deleteFilmById(filmId);
    }
}
