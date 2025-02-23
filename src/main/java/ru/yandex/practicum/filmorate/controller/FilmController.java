package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.utils.BooleanAnswerBuilder.*;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        List<Film> films = filmService.getAllFilms();
        log.info("Successfully get films");
        return films;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Successfully update film {}", updatedFilm);
        return updatedFilm;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        Film addedFilm = filmService.addFilm(film);
        log.info("Successfully add film {}", addedFilm);
        return addedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public Map<String, String> addFilmLike(@PathVariable long id, @PathVariable long userId) {
        boolean isSuccess = filmService.addFilmLike(id, userId);
        return isSuccess ? addLikeSuccessAnswer(id, userId) : addLikeFailAnswer(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Map<String, String> deleteFilmLike(@PathVariable long id, @PathVariable long userId) {
        boolean isSuccess = filmService.deleteFilmLike(id, userId);
        return isSuccess ? deleteLikeSuccessAnswer(id, userId) : deleteLikeFailAnswer(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}
