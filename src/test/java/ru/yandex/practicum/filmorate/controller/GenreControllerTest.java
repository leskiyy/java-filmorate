package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreControllerTest {

    @Mock
    GenreService service;

    @InjectMocks
    GenreController controller;

    @Test
    void getAllGenres() {
        Genre expectedGenre = new Genre();
        when(service.getAllGenres()).thenReturn(List.of(expectedGenre));

        List<Genre> actualGenres = controller.getAllGenres();

        verify(service, times(1)).getAllGenres();
        assertEquals(1, actualGenres.size());
        assertEquals(expectedGenre, actualGenres.getFirst());
    }

    @Test
    void getGenreById_whenGenreIsFound() {
        Genre expectedGenre = new Genre();
        when(service.getGenreById(1)).thenReturn(expectedGenre);

        Genre actualGenre = controller.getGenreById(1);

        verify(service, times(1)).getGenreById(1);
        assertEquals(expectedGenre, actualGenre);
    }

    @Test
    void getGenreById_whenGenreIsNotFound() {
        when(service.getGenreById(1)).thenThrow(new NotFoundException("test"));

        Throwable throwable = assertThrows(NotFoundException.class, () -> controller.getGenreById(1));

        verify(service, times(1)).getGenreById(1);
        assertEquals("test", throwable.getMessage());
    }

}