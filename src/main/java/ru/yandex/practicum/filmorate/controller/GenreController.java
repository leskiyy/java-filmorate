package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService service;

    @GetMapping
    public List<Genre> getAllGenres() {
        List<Genre> allGenres = service.getAllGenres();
        log.info("Successfully get all genres");
        return allGenres;
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        Genre genreById = service.getGenreById(id);
        log.info("Successfully get mpa with id={}", id);
        return genreById;
    }

}
