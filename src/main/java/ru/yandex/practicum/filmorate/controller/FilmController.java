package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
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
    public List<FilmDTO> getAllFilms() {
        List<FilmDTO> films = filmService.getAllFilms();
        log.info("Successfully get films");
        return films;
    }

    @GetMapping("/{id}")
    public FilmDTO getFilmById(@PathVariable long id) {
        FilmDTO film = filmService.getFilmById(id);
        log.info("Successfully get film{}", film);
        return film;
    }

    @PutMapping
    public FilmDTO updateFilm(@RequestBody FilmDTO film) {
        FilmDTO updatedFilm = filmService.updateFilm(film);
        log.info("Successfully update film {}", updatedFilm);
        return updatedFilm;
    }

    @PostMapping
    public FilmDTO addFilm(@RequestBody FilmDTO film) {
        FilmDTO addedFilm = filmService.addFilm(film);
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
    public List<FilmDTO> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}
