package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    GenreRepository repository;

    @InjectMocks
    GenreService service;

    @Test
    void getAllGenres() {
        List<Genre> expectedGenres = List.of(new Genre());

        when(repository.findAll()).thenReturn(expectedGenres);

        List<Genre> actualGenres = service.getAllGenres();

        verify(repository, times(1)).findAll();
        assertSame(expectedGenres, actualGenres);
        assertSame(expectedGenres.getFirst(), actualGenres.getFirst());
    }

    @Test
    void getGenreById_whenGenreExists() {
        Genre expectedGenre = new Genre();
        when(repository.findById(1)).thenReturn(Optional.of(expectedGenre));

        Genre actualGenre = service.getGenreById(1);

        verify(repository, times(1)).findById(1);
        assertSame(expectedGenre, actualGenre);
    }

    @Test
    void getGenreById_whenGenreNotExists() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.getGenreById(1));
        assertEquals("There is no genre with id=1", throwable.getMessage());
    }
}