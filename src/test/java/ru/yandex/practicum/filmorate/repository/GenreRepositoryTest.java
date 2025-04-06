package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreRepository.class, GenreRowMapper.class})
class GenreRepositoryTest {

    private final GenreRepository repository;

    @Test
    void findById_whenIsPresentTrue() {
        Optional<Genre> genreOptional = repository.findById(1);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre -> {
                    assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
                });
    }

    @Test
    void findById_whenIsPresentFalse() {
        Optional<Genre> genreOptional = repository.findById(7);
        assertThat(genreOptional)
                .isEmpty();
    }

    @Test
    void findAll() {
        List<Genre> genres = repository.findAll();
        assertEquals(6, genres.size());
    }

    @Test
    void existById_whenTrue() {
        boolean existById = repository.existById(1);
        assertTrue(existById);
    }

    @Test
    void existById_whenFalse() {
        boolean existById = repository.existById(7);
        assertFalse(existById);
    }
}