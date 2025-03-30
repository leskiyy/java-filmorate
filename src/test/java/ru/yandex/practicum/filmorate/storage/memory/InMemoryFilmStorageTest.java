package ru.yandex.practicum.filmorate.storage.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {

    private static FilmStorage filmStorage;

    @BeforeEach
    void initStorage() {
        filmStorage = new InMemoryFilmStorage(new InMemoryGenreStorage());
    }


    @Test
    void findAll() {
        addFiveFilmsFilms();
        List<Film> films = filmStorage.findAll();

        assertEquals(5, films.size());
        assertEquals("name1", films.getFirst().getName());
        assertEquals("name5", films.getLast().getName());
    }

    @Test
    void save() {
        Film filmToSave = new Film()
                .setName("name1")
                .setDescription("desc")
                .setReleaseDate(LocalDate.of(2020, 1, 1))
                .setDuration(121)
                .setMpa(1);
        filmStorage.save(filmToSave);
        List<Film> films = filmStorage.findAll();

        assertEquals(1, films.size());
        assertSame(filmToSave, films.getFirst());
    }

    @Test
    void updateGenres() {
        addFiveFilmsFilms();

        List<Genre> genresByFilmIdBefore = filmStorage.findGenresByFilmId(1L);

        filmStorage.updateGenres(List.of(
                new Genre(6, null),
                new Genre(4, null)), 1L);

        List<Genre> genresByFilmIdAfter = filmStorage.findGenresByFilmId(1L);

        assertNotEquals(genresByFilmIdBefore, genresByFilmIdAfter);
        assertEquals(2, genresByFilmIdAfter.size());
        assertEquals(4, genresByFilmIdAfter.getFirst().getId());
        assertEquals("Триллер", genresByFilmIdAfter.getFirst().getName());
        assertEquals(6, genresByFilmIdAfter.getLast().getId());
        assertEquals("Боевик", genresByFilmIdAfter.getLast().getName());
    }

    @Test
    void update_whenFilmFound() {
        Film filmToUpdate = new Film()
                .setId(1L)
                .setName("name1 updated")
                .setDescription("desc updated")
                .setReleaseDate(LocalDate.of(2020, 1, 1))
                .setDuration(121)
                .setMpa(3);
        addFiveFilmsFilms();

        Film updated = filmStorage.update(filmToUpdate);

        assertSame(filmToUpdate, updated);
    }

    @Test
    void update_whenFilmNotFound() {
        Film filmToUpdate = new Film()
                .setId(6L)
                .setName("name1 updated")
                .setDescription("desc updated")
                .setReleaseDate(LocalDate.of(2020, 1, 1))
                .setDuration(121)
                .setMpa(3);
        addFiveFilmsFilms();

        Throwable throwable = assertThrows(NotFoundException.class, () -> filmStorage.update(filmToUpdate));

        assertEquals("There is no film with id=6", throwable.getMessage());
    }

    @Test
    void findById_whenFound() {
        addFiveFilmsFilms();

        Optional<Film> byId = filmStorage.findById(2L);

        assertTrue(byId.isPresent());
        Film film = byId.get();

        assertEquals(2L, film.getId());
        assertEquals("name2", film.getName());
        assertEquals(LocalDate.of(2020, 1, 2), film.getReleaseDate());
    }

    @Test
    void findById_whenNotFound() {
        addFiveFilmsFilms();

        Optional<Film> byId = filmStorage.findById(6L);

        assertTrue(byId.isEmpty());
    }

    @Test
    void deleteById_whenExists() {
        addFiveFilmsFilms();

        boolean deleted = filmStorage.deleteById(3);

        assertTrue(deleted);

        List<Film> films = filmStorage.findAll();
        assertEquals(4, films.size());
    }

    @Test
    void deleteById_whenNotExists() {
        addFiveFilmsFilms();

        boolean deleted = filmStorage.deleteById(6);

        assertFalse(deleted);

        List<Film> films = filmStorage.findAll();
        assertEquals(5, films.size());
    }

    @Test
    void existById_whenTrue() {
        addFiveFilmsFilms();

        boolean existById = filmStorage.existById(4L);
        assertTrue(existById);
    }

    @Test
    void existById_whenFalse() {
        addFiveFilmsFilms();

        boolean existById = filmStorage.existById(12L);
        assertFalse(existById);
    }

    @Test
    void addLike_whenLikeIsNotSet() {
        addFiveFilmsFilms();

        int rateBefore = filmStorage.rateByFilmId(2L);
        boolean addLike = filmStorage.addLike(2, 10);
        int rateAfter = filmStorage.rateByFilmId(2L);

        assertTrue(addLike);
        assertEquals(rateBefore + 1, rateAfter);
    }

    @Test
    void addLike_whenLikeIsSet() {
        addFiveFilmsFilms();

        int rateBefore = filmStorage.rateByFilmId(2L);
        boolean addLike = filmStorage.addLike(2, 1);
        int rateAfter = filmStorage.rateByFilmId(2L);

        assertFalse(addLike);
        assertEquals(rateBefore, rateAfter);
    }

    @Test
    void removeLike_whenLikeIsNotSet() {
        addFiveFilmsFilms();

        int rateBefore = filmStorage.rateByFilmId(2L);
        boolean removeLike = filmStorage.removeLike(2, 10);
        int rateAfter = filmStorage.rateByFilmId(2L);

        assertFalse(removeLike);
        assertEquals(rateBefore, rateAfter);
    }

    @Test
    void removeLike_whenLikeIsSet() {
        addFiveFilmsFilms();

        int rateBefore = filmStorage.rateByFilmId(2L);
        boolean removeLike = filmStorage.removeLike(2, 1);
        int rateAfter = filmStorage.rateByFilmId(2L);

        assertTrue(removeLike);
        assertEquals(rateBefore - 1, rateAfter);
    }

    @Test
    void findGenresByFilmId() {
        addFiveFilmsFilms();

        List<Genre> genresByFilmId1 = filmStorage.findGenresByFilmId(1L);
        assertEquals(3, genresByFilmId1.size());
        assertEquals(1, genresByFilmId1.getFirst().getId());
        assertEquals("Комедия", genresByFilmId1.getFirst().getName());
        assertEquals(3, genresByFilmId1.getLast().getId());
        assertEquals("Мультфильм", genresByFilmId1.getLast().getName());

        List<Genre> genresByFilmId2 = filmStorage.findGenresByFilmId(2);
        assertEquals(3, genresByFilmId2.size());
        assertEquals(2, genresByFilmId2.getFirst().getId());
        assertEquals("Драма", genresByFilmId2.getFirst().getName());
        assertEquals(4, genresByFilmId2.getLast().getId());
        assertEquals("Триллер", genresByFilmId2.getLast().getName());
    }

    @Test
    void rateByFilmId() {
        addFiveFilmsFilms();

        int rate1 = filmStorage.rateByFilmId(1);
        int rate2 = filmStorage.rateByFilmId(2);
        int rate3 = filmStorage.rateByFilmId(3);

        assertEquals(1, rate1);
        assertEquals(2, rate2);
        assertEquals(3, rate3);
    }

    private void addFiveFilmsFilms() {
        filmStorage.save(new Film()
                .setName("name1")
                .setDescription("desc")
                .setReleaseDate(LocalDate.of(2020, 1, 1))
                .setDuration(121)
                .setMpa(1));
        filmStorage.save(new Film()
                .setName("name2")
                .setDescription("desc")
                .setReleaseDate(LocalDate.of(2020, 1, 2))
                .setDuration(122)
                .setMpa(2));
        filmStorage.save(new Film()
                .setName("name3")
                .setDescription("desc")
                .setReleaseDate(LocalDate.of(2020, 1, 3))
                .setDuration(123)
                .setMpa(3));
        filmStorage.save(new Film()
                .setName("name4")
                .setDescription("desc")
                .setReleaseDate(LocalDate.of(2020, 1, 4))
                .setDuration(124)
                .setMpa(4));
        filmStorage.save(new Film()
                .setName("name5")
                .setDescription("desc")
                .setReleaseDate(LocalDate.of(2020, 1, 5))
                .setDuration(125)
                .setMpa(5));

        filmStorage.addLike(1, 1);
        filmStorage.addLike(2, 1);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(3, 1);
        filmStorage.addLike(3, 2);
        filmStorage.addLike(3, 3);

        filmStorage.updateGenres(List.of(new Genre(1, null),
                new Genre(2, null),
                new Genre(3, null)), 1);
        filmStorage.updateGenres(List.of(new Genre(2, null),
                new Genre(3, null),
                new Genre(4, null)), 2);

    }
}