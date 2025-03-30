package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final GenreStorage genreStorage;

    private final AtomicLong id = new AtomicLong(0L);
    private final Map<Long, Film> films = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> likes = new ConcurrentHashMap<>();
    private final Map<Long, Set<Integer>> filmsGenres = new ConcurrentHashMap<>();

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film save(Film film) {
        film.setId(nextId());
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public void updateGenres(List<Genre> genres, long id) {
        if (genres == null) {
            filmsGenres.put(id, new HashSet<>());
        } else {
            filmsGenres.put(id, new HashSet<>(genres.stream().map(Genre::getId).toList()));
        }
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("There is no film with id=" + film.getId());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> findById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public boolean deleteById(long id) {
        likes.remove(id);
        filmsGenres.remove(id);
        return films.remove(id, films.get(id));
    }

    @Override
    public boolean existById(long id) {
        return films.get(id) != null;
    }

    @Override
    public boolean addLike(long id, long userId) {
        return likes.get(id).add(userId);
    }

    @Override
    public boolean removeLike(long id, long userId) {
        return likes.get(id).remove(userId);
    }

    @Override
    public List<Genre> findGenresByFilmId(long id) {
        Set<Integer> genreIds = filmsGenres.get(id);
        return genreStorage.findAll().stream().filter(el -> genreIds.contains(el.getId())).toList();
    }

    @Override
    public int rateByFilmId(long id) {
        return likes.get(id).size();
    }

    private long nextId() {
        return id.incrementAndGet();
    }

}

