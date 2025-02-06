package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmStorage {

    private static Long id = 0L;
    private final Map<Long, Film> films = new HashMap<>();

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film addFilm(Film film) {
        film.setId(nextId());
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("There is no film with id=" + film.getId());
        }
        films.put(film.getId(), film);
        return film;
    }

    private long nextId() {
        return ++id;
    }

}
