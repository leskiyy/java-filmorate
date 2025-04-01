package ru.yandex.practicum.filmorate.storage.memory;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryGenreStorageTest {

    private static final GenreStorage storage = new InMemoryGenreStorage();

    @Test
    void findById_whenFound() {
        Optional<Genre> firstMpa = storage.findById(1);
        assertTrue(firstMpa.isPresent());
        assertEquals(new Genre(1, "Комедия"), firstMpa.get());
    }

    @Test
    void findById_whenNotFound() {
        Optional<Genre> first = storage.findById(7);
        assertFalse(first.isPresent());
    }

    @Test
    void findAll() {
        List<Genre> all = storage.findAll();
        assertEquals(6, all.size());
        assertEquals(1, all.getFirst().getId());
        assertEquals(6, all.getLast().getId());
    }

    @Test
    void existById_whenTrue() {
        assertTrue(storage.existById(1));
        assertTrue(storage.existById(2));
        assertTrue(storage.existById(3));
        assertTrue(storage.existById(4));
        assertTrue(storage.existById(5));
        assertTrue(storage.existById(6));
    }

    @Test
    void existById_whenFalse() {
        assertFalse(storage.existById(7));
    }
}