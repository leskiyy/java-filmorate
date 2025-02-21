package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;
    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private FilmService service;

    @Test
    void getAllFilms() {
        List<Film> expectedFilms = List.of(new Film());
        when(filmStorage.getAllFilms()).thenReturn(expectedFilms);

        List<Film> actualFilms = service.getAllFilms();

        verify(filmStorage, times(1)).getAllFilms();
        assertEquals(expectedFilms.size(), actualFilms.size());
        assertSame(expectedFilms.getFirst(), actualFilms.getFirst());
    }

    @Test
    void updateFilm_whenFilmIsFound() {
        Film expectedFilm = new Film();
        Film filmToUpdate = new Film();
        when(filmStorage.updateFilm(filmToUpdate)).thenReturn(expectedFilm);

        Film actualFilm = service.updateFilm(filmToUpdate);

        verify(filmStorage, times(1)).updateFilm(filmToUpdate);
        assertSame(expectedFilm, actualFilm);
    }

    @Test
    void updateFilm_whenFilmIsNotFound() {
        Film filmToUpdate = new Film();
        when(filmStorage.updateFilm(filmToUpdate)).thenThrow(new NotFoundException("test"));

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.updateFilm(filmToUpdate));

        verify(filmStorage, times(1)).updateFilm(filmToUpdate);
        assertEquals("test", throwable.getMessage());
    }

    @Test
    void addFilm() {
        Film filmToAdd = new Film();
        Film expectedFilm = new Film();
        when(filmStorage.addFilm(filmToAdd)).thenReturn(expectedFilm);

        Film actualFilm = service.addFilm(filmToAdd);

        verify(filmStorage, times(1)).addFilm(filmToAdd);
        assertSame(expectedFilm, actualFilm);
    }

    @Test
    void addFilmLike_whenFilmIsNotExist() {
        long filmId = 2L;
        long userId = 10L;
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.empty());
        when(userStorage.getUserById(userId)).thenReturn(Optional.of(new User()));

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.addFilmLike(filmId, userId));
        verify(filmStorage, times(1)).getFilmById(filmId);
        assertEquals("There is no film with id=" + filmId, throwable.getMessage());
    }

    @Test
    void addFilmLike_whenUserIsNotExist() {
        long filmId = 2L;
        long userId = 10L;
        when(userStorage.getUserById(userId)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.addFilmLike(filmId, userId));
        verify(userStorage, times(1)).getUserById(userId);
        assertEquals("There is no user with id=" + userId, throwable.getMessage());
    }

    @Test
    void addFilmLike_whenLikeIsAlreadySet() {
        long filmId = 2L;
        long userId = 10L;

        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(new Film()));
        when(userStorage.getUserById(userId)).thenReturn(Optional.of(new User()));
        when(filmStorage.addLike(filmId, userId)).thenReturn(false);

        assertFalse(service.addFilmLike(filmId, userId));

        verify(userStorage, times(1)).getUserById(userId);
        verify(filmStorage, times(1)).getFilmById(filmId);
    }

    @Test
    void addFilmLike_whenLikeIsNotSet() {
        long filmId = 2L;
        long userId = 10L;

        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(new Film()));
        when(userStorage.getUserById(userId)).thenReturn(Optional.of(new User()));
        when(filmStorage.addLike(filmId, userId)).thenReturn(true);

        assertTrue(service.addFilmLike(filmId, userId));

        verify(userStorage, times(1)).getUserById(userId);
        verify(filmStorage, times(1)).getFilmById(filmId);
    }

    @Test
    void deleteFilmLike_whenFilmIsNotExist() {
        long filmId = 2L;
        long userId = 10L;
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.empty());
        when(userStorage.getUserById(userId)).thenReturn(Optional.of(new User()));

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.deleteFilmLike(filmId, userId));
        verify(filmStorage, times(1)).getFilmById(filmId);
        assertEquals("There is no film with id=" + filmId, throwable.getMessage());
    }

    @Test
    void deleteFilmLike_whenUserIsNotExist() {
        long filmId = 2L;
        long userId = 10L;
        when(userStorage.getUserById(userId)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.deleteFilmLike(filmId, userId));
        verify(userStorage, times(1)).getUserById(userId);
        assertEquals("There is no user with id=" + userId, throwable.getMessage());
    }

    @Test
    void deleteFilmLike_whenLikeIsAlreadySet() {
        long filmId = 2L;
        long userId = 10L;

        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(new Film()));
        when(userStorage.getUserById(userId)).thenReturn(Optional.of(new User()));
        when(filmStorage.removeLike(filmId, userId)).thenReturn(true);

        assertTrue(service.deleteFilmLike(filmId, userId));

        verify(userStorage, times(1)).getUserById(userId);
        verify(filmStorage, times(1)).getFilmById(filmId);
    }

    @Test
    void deleteFilmLike_whenLikeIsNotSet() {
        long filmId = 2L;
        long userId = 10L;

        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(new Film()));
        when(userStorage.getUserById(userId)).thenReturn(Optional.of(new User()));
        when(filmStorage.removeLike(filmId, userId)).thenReturn(false);

        assertFalse(service.deleteFilmLike(filmId, userId));

        verify(userStorage, times(1)).getUserById(userId);
        verify(filmStorage, times(1)).getFilmById(filmId);
    }

    @Test
    void getPopularFilms() {
        int count = 2;
        List<Film> returnFilms = List.of(new Film());
        List<Film> expectedFilms = List.of();
        when(filmStorage.getAllFilms()).thenReturn(returnFilms);

        List<Film> popularFilms = service.getPopularFilms(count);
    }
}