package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {

    @Mock
    private FilmService service;

    @InjectMocks
    private FilmController controller;

    @Test
    void getAllFilms() {
        List<FilmDTO> expectedFilms = List.of(new FilmDTO());
        when(service.getAllFilms()).thenReturn(expectedFilms);

        List<FilmDTO> actualFilms = controller.getAllFilms();

        verify(service, times(1)).getAllFilms();
        assertEquals(expectedFilms.size(), actualFilms.size());
        assertSame(expectedFilms.getFirst(), actualFilms.getFirst());
    }

    @Test
    void updateFilm_whenFilmIsFound() {
        FilmDTO expectedFilm = new FilmDTO();
        FilmDTO filmToUpdate = new FilmDTO();
        when(service.updateFilm(filmToUpdate)).thenReturn(expectedFilm);

        FilmDTO actualFilm = controller.updateFilm(filmToUpdate);

        verify(service, times(1)).updateFilm(filmToUpdate);
        assertSame(expectedFilm, actualFilm);
    }

    @Test
    void updateFilm_whenFilmIsNotFound() {
        FilmDTO filmToUpdate = new FilmDTO();
        when(service.updateFilm(filmToUpdate)).thenThrow(new NotFoundException("test"));

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> controller.updateFilm(filmToUpdate));

        verify(service, times(1)).updateFilm(filmToUpdate);
        assertEquals("test", throwable.getMessage());
    }

    @Test
    void addFilm() {
        FilmDTO filmToAdd = new FilmDTO();
        FilmDTO expectedFilm = new FilmDTO();
        when(service.addFilm(filmToAdd)).thenReturn(expectedFilm);

        FilmDTO actualFilm = controller.addFilm(filmToAdd);

        verify(service, times(1)).addFilm(filmToAdd);
        assertSame(expectedFilm, actualFilm);
    }

    @Test
    void addFilmLike_whenTrue() {
        when(service.addFilmLike(1, 1)).thenReturn(true);

        Map<String, String> response = controller.addFilmLike(1, 1);

        verify(service, times(1)).addFilmLike(1, 1);
        assertEquals("Like to film id=1 was added by user id=1 ", response.get("SUCCESS"));
    }

    @Test
    void addFilmLike_whenFalse() {
        when(service.addFilmLike(1, 1)).thenReturn(false);

        Map<String, String> response = controller.addFilmLike(1, 1);

        verify(service, times(1)).addFilmLike(1, 1);
        assertEquals("Like to film id=1 is already added by user id=1", response.get("FAIL"));
    }

    @Test
    void deleteFilmLike_whenTrue() {
        when(service.deleteFilmLike(1, 1)).thenReturn(true);

        Map<String, String> response = controller.deleteFilmLike(1, 1);

        verify(service, times(1)).deleteFilmLike(1, 1);
        assertEquals("Like to film id=1 was deleted by user id=1", response.get("SUCCESS"));
    }

    @Test
    void deleteFilmLike_whenFalse() {
        when(service.deleteFilmLike(1, 1)).thenReturn(false);

        Map<String, String> response = controller.deleteFilmLike(1, 1);

        verify(service, times(1)).deleteFilmLike(1, 1);
        assertEquals("There is no like to remove for film id=1 added by user id=1", response.get("FAIL"));
    }

    @Test
    void getPopularFilms() {
        FilmDTO expectedFilm = new FilmDTO();
        when(service.getPopularFilms(1)).thenReturn(List.of(expectedFilm));

        List<FilmDTO> actualFilms = controller.getPopularFilms(1);

        verify(service, times(1)).getPopularFilms(1);
        assertEquals(1, actualFilms.size());
        assertSame(expectedFilm, actualFilms.getFirst());
    }

    @Test
    void getFilmById() {
        FilmDTO expected = new FilmDTO();
        when(service.getFilmById(1L)).thenReturn(expected);

        FilmDTO actual = controller.getFilmById(1L);
        verify(service, times(1)).getFilmById(1L);
        assertSame(expected, actual);
    }
}