package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryGenreStorage implements GenreStorage {

    private final Map<Integer, Genre> genres = Map.of(1, new Genre(1, "Комедия"),
            2, new Genre(2, "Драма"),
            3, new Genre(3, "Мультфильм"),
            4, new Genre(4, "Триллер"),
            5, new Genre(5, "Документальный"),
            6, new Genre(6, "Боевик"));

    @Override
    public Optional<Genre> findById(int id) {
        return Optional.ofNullable(genres.get(id));
    }

    @Override
    public List<Genre> findAll() {
        return genres.values().stream().sorted(Comparator.comparingInt(Genre::getId)).toList();
    }

    @Override
    public boolean existById(int id) {
        return genres.get(id) != null;
    }
}
