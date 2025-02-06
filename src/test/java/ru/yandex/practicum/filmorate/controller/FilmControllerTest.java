package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {

    @Mock
    private FilmStorage filmStorage;

    @InjectMocks
    private FilmController controller;

    private static final Film film = new Film(1L, "film", "desc",
            LocalDate.of(2010, 1, 1), 102);

    private static final Film expectedFilm = new Film(1L, "name", "desc",
            LocalDate.of(1999, 1, 1), 12);
    @Test
    void getAllFilms() {
        List<Film> expectedFilms = List.of(expectedFilm);
        when(filmStorage.getAllFilms()).thenReturn(expectedFilms);

        List<Film> actualFilms = controller.getAllFilms();

        verify(filmStorage, times(1)).getAllFilms();
        assertEquals(expectedFilms.size(), actualFilms.size());
        assertEquals(expectedFilms.getFirst(), actualFilms.getFirst());
    }

    @Test
    void updateFilm_whenFilmIsFound() {
        when(filmStorage.updateFilm(film)).thenReturn(expectedFilm);

        Film actualFilm = controller.updateFilm(film);

        verify(filmStorage, times(1)).updateFilm(film);
        assertEquals(expectedFilm, actualFilm);
    }

    @Test
    void updateFilm_whenFilmIsNotFound() {
        when(filmStorage.updateFilm(film)).thenThrow(new NotFoundException("test"));

        Throwable throwable = assertThrows(ResponseStatusException.class,
                () -> controller.updateFilm(film));

        verify(filmStorage, times(1)).updateFilm(film);
        assertEquals("404 NOT_FOUND \"test\"", throwable.getMessage());
    }

    @Test
    void addFilm() {
        when(filmStorage.addFilm(film)).thenReturn(expectedFilm);

        Film actualFilm = controller.addFilm(film);

        verify(filmStorage, times(1)).addFilm(film);

        assertEquals(expectedFilm, actualFilm);
    }
}