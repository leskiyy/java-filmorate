package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Repository
public class InMemoryFilmStorage implements FilmStorage {

    private static final AtomicLong id = new AtomicLong(0L);
    private static final Map<Long, Film> films = new ConcurrentHashMap<>();
    private static final Map<Long, Set<Long>> likes = new ConcurrentHashMap<>();

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(nextId());
        film.setRate(0);
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("There is no film with id=" + film.getId());
        }
        film.setRate(likes.get(film.getId()).size());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(films.get(id));
    }


    @Override
    public boolean addLike(long id, long userId) {
        boolean add = likes.get(id).add(userId);
        updateFilmLikes(id);
        return add;
    }

    @Override
    public boolean removeLike(long id, long userId) {
        boolean remove = likes.get(id).remove(userId);
        updateFilmLikes(id);
        return remove;
    }

    private void updateFilmLikes(long id) {
        Film film = films.get(id);
        film.setRate(likes.get(id).size());
    }

    private long nextId() {
        return id.incrementAndGet();
    }

}
