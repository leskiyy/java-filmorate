package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaRepository.class, MpaRowMapper.class})
class MpaRepositoryTest {

    private final MpaRepository repository;

    @Test
    void findById_whenIsPresentTrue() {
        Optional<Mpa> mpaOptional = repository.findById(1);
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(genre -> {
                    assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(genre).hasFieldOrPropertyWithValue("name", "G");
                });
    }

    @Test
    void findById_whenIsPresentFalse() {
        Optional<Mpa> mpaOptional = repository.findById(6);
        assertThat(mpaOptional)
                .isEmpty();
    }

    @Test
    void findAll() {
        List<Mpa> allMpa = repository.findAll();
        assertEquals(5, allMpa.size());
    }

    @Test
    void existById_whenTrue() {
        boolean existById = repository.existById(1);
        assertTrue(existById);
    }

    @Test
    void existById_whenFalse() {
        boolean existById = repository.existById(6);
        assertFalse(existById);
    }
}