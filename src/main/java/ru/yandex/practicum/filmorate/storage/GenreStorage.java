package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    Optional<Genre> findById(int id);

    List<Genre> findAll();

    boolean existById(int id);
}
