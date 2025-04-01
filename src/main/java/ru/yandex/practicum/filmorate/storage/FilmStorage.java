package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Film save(Film film);

    Film update(Film film);

    Optional<Film> findById(long id);

    boolean deleteById(long id);

    boolean existById(long id);

    boolean addLike(long id, long userId);

    boolean removeLike(long id, long userId);

    List<Genre> findGenresByFilmId(long id);

    int rateByFilmId(long id);

    void updateGenres(List<Genre> genres, long id);
}
