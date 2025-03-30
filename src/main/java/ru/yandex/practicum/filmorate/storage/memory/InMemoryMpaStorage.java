package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryMpaStorage implements MpaStorage {

    private final Map<Integer, Mpa> mpa = Map.of(1, new Mpa(1, "G"),
            2, new Mpa(2, "PG"),
            3, new Mpa(3, "PG-13"),
            4, new Mpa(4, "R"),
            5, new Mpa(5, "NC-17"));

    @Override
    public Optional<Mpa> findById(int id) {
        return Optional.ofNullable(mpa.get(id));
    }

    @Override
    public List<Mpa> findAll() {
        return mpa.values().stream().sorted(Comparator.comparingInt(Mpa::getId)).toList();
    }

    @Override
    public boolean existById(int id) {
        return mpa.get(id) != null;
    }
}
