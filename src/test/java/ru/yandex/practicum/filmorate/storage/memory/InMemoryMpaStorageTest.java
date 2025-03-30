package ru.yandex.practicum.filmorate.storage.memory;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryMpaStorageTest {

    private static final MpaStorage storage = new InMemoryMpaStorage();

    @Test
    void findById_whenFound() {
        Optional<Mpa> firstMpa = storage.findById(1);
        assertTrue(firstMpa.isPresent());
        assertEquals(new Mpa(1, "G"), firstMpa.get());
    }

    @Test
    void findById_whenNotFound() {
        Optional<Mpa> firstMpa = storage.findById(6);
        assertFalse(firstMpa.isPresent());
    }

    @Test
    void findAll() {
        List<Mpa> all = storage.findAll();
        assertEquals(5, all.size());
        assertEquals(1, all.getFirst().getId());
        assertEquals(5, all.getLast().getId());
    }

    @Test
    void existById_whenTrue() {
        assertTrue(storage.existById(1));
        assertTrue(storage.existById(2));
        assertTrue(storage.existById(3));
        assertTrue(storage.existById(4));
        assertTrue(storage.existById(5));
    }

    @Test
    void existById_whenFalse() {
        assertFalse(storage.existById(6));
    }
}