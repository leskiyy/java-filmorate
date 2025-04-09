package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DirectorRepository.class, DirectorRowMapper.class})
public class DirectorRepositoryTest {
    private final DirectorRepository repository;

    @Test
    void findById_whenIsPresentTrue() {
        addDirectors();
        Optional<Director> directorOptional = repository.findById(1);
        assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(director -> {
                    assertThat(director).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(director).hasFieldOrPropertyWithValue("name", "Quentin Tarantino");
                });
    }

    @Test
    void findAll_whenIsPresentTrue() {
        addDirectors();
        List<Director> directors = repository.findAll();
        Assertions.assertEquals(2, directors.size());
        Assertions.assertEquals("Guy Ritchie", directors.get(1).getName());
    }

    @Test
    void addDirectorTest() {
        Director director = new Director();
        director.setName("Tony Scott");
        Director result = repository.addDirector(director);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("Tony Scott", result.getName());
    }

    @Test
    void updateDirectorTest() {
        addDirectors();
        Optional<Director> directorOptional = repository.findById(1);
        assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(director -> {
                    assertThat(director).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(director).hasFieldOrPropertyWithValue("name", "Quentin Tarantino");
                });
        Director directorForUpdate = directorOptional.get();
        directorForUpdate.setName("Another director");
        Director updatedDirector = repository.updateDirector(directorForUpdate);
        Assertions.assertEquals(1, updatedDirector.getId());
        Assertions.assertEquals("Another director", updatedDirector.getName());
    }

    @Test
    void deleteDirectorTest() {
        addDirectors();
        Assertions.assertEquals(2, repository.findAll().size());
        repository.deleteDirector(1);
        Assertions.assertEquals(1, repository.findAll().size());
    }

    private void addDirectors() {
        Director director = new Director();
        director.setName("Quentin Tarantino");
        repository.addDirector(director);
        director.setName("Guy Ritchie");
        repository.addDirector(director);
    }

}
