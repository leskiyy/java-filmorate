package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.FilmMapper;

import java.util.Collections;
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
    @Mock
    private MpaStorage mpaStorage;
    @Mock
    private GenreStorage genreStorage;

    @InjectMocks
    private FilmService service;

    @Test
    void getAllFilms() {
        FilmDTO expectedFilm = new FilmDTO().setId(1L)
                .setMpa(new Mpa(4, "TestMpa"))
                .setRate(5)
                .setGenres(List.of(new Genre(1, "TestGenre")));
        List<FilmDTO> expectedFilms = List.of(expectedFilm);
        when(filmStorage.findAll()).thenReturn(List.of(new Film().setId(1L).setMpa(4)));
        when(filmStorage.rateByFilmId(1L)).thenReturn(5);
        when(filmStorage.findGenresByFilmId(1L)).thenReturn(List.of(new Genre(1, "TestGenre")));
        when(mpaStorage.findById(4)).thenReturn(Optional.of(new Mpa(4, "TestMpa")));

        List<FilmDTO> actualFilms = service.getAllFilms();
        FilmDTO actualFilm = actualFilms.getFirst();

        verify(filmStorage, times(1)).findAll();
        verify(filmStorage, times(1)).rateByFilmId(1L);
        verify(filmStorage, times(1)).findGenresByFilmId(1L);
        verify(mpaStorage, times(1)).findById(4);
        assertEquals(expectedFilms.size(), actualFilms.size());
        assertEquals(expectedFilms.getFirst(), actualFilms.getFirst());
        assertEquals(expectedFilm.getId(), actualFilm.getId());
        assertEquals(expectedFilm.getRate(), actualFilm.getRate());
        assertEquals(expectedFilm.getMpa().getName(), actualFilm.getMpa().getName());
        assertEquals(expectedFilm.getMpa().getId(), actualFilm.getMpa().getId());
        assertEquals(expectedFilm.getGenres().size(), actualFilm.getGenres().size());
        assertEquals(expectedFilm.getGenres().getFirst().getName(), actualFilm.getGenres().getFirst().getName());
        assertEquals(expectedFilm.getGenres().getFirst().getId(), actualFilm.getGenres().getFirst().getId());

    }

    @Test
    void updateFilm_whenFilmIsFound() {
        FilmDTO filmToUpdate = new FilmDTO().setId(1L)
                .setMpa(new Mpa(4, "TestMpa"))
                .setRate(5)
                .setGenres(List.of(new Genre(1, "TestGenre")));

        when(filmStorage.existById(1L)).thenReturn(true);
        when(mpaStorage.existById(4)).thenReturn(true);
        when(genreStorage.existById(1)).thenReturn(true);
        when(filmStorage.rateByFilmId(1L)).thenReturn(2);

        FilmDTO actualFilm = service.updateFilm(filmToUpdate);

        verify(filmStorage, times(1)).update(FilmMapper.mapToFilm(filmToUpdate));
        verify(filmStorage, times(1)).existById(1L);
        verify(filmStorage, times(1)).rateByFilmId(1L);
        verify(mpaStorage, times(1)).existById(4);
        verify(genreStorage, times(1)).existById(1);

        assertSame(filmToUpdate, actualFilm);
        assertEquals(2, actualFilm.getRate());
    }

    @Test
    void updateFilm_whenFilmIsNotFound() {
        FilmDTO filmToUpdate = new FilmDTO().setId(1L)
                .setMpa(new Mpa(4, "TestMpa"))
                .setRate(5)
                .setGenres(List.of(new Genre(1, "TestGenre")));

        when(filmStorage.existById(1L)).thenReturn(false);

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.updateFilm(filmToUpdate));

        verify(filmStorage, only()).existById(1L);
        verify(mpaStorage, never()).existById(4);
        verify(genreStorage, never()).existById(1);
        verify(filmStorage, never()).update(FilmMapper.mapToFilm(filmToUpdate));
        verify(filmStorage, never()).rateByFilmId(1L);
        assertEquals("There is no film with id=1", throwable.getMessage());
    }

    @Test
    void updateFilm_whenMpaIsNotFound() {
        FilmDTO filmToUpdate = new FilmDTO().setId(1L)
                .setMpa(new Mpa(4, "TestMpa"))
                .setRate(5)
                .setGenres(List.of(new Genre(1, "TestGenre")));

        when(filmStorage.existById(1L)).thenReturn(true);
        when(mpaStorage.existById(4)).thenReturn(false);

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.updateFilm(filmToUpdate));

        verify(filmStorage, only()).existById(1L);
        verify(mpaStorage, only()).existById(4);
        verify(genreStorage, never()).existById(1);
        verify(filmStorage, never()).update(FilmMapper.mapToFilm(filmToUpdate));
        verify(filmStorage, never()).rateByFilmId(1L);
        assertEquals("There is no mpa with id=4", throwable.getMessage());
    }

    @Test
    void updateFilm_whenGenreIsNotFound() {
        FilmDTO filmToUpdate = new FilmDTO().setId(1L)
                .setMpa(new Mpa(4, "TestMpa"))
                .setRate(5)
                .setGenres(List.of(new Genre(1, "TestGenre")));

        when(filmStorage.existById(1L)).thenReturn(true);
        when(mpaStorage.existById(4)).thenReturn(true);
        when(genreStorage.existById(1)).thenReturn(false);

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.updateFilm(filmToUpdate));

        verify(filmStorage, only()).existById(1L);
        verify(mpaStorage, only()).existById(4);
        verify(genreStorage, only()).existById(1);
        verify(filmStorage, never()).update(FilmMapper.mapToFilm(filmToUpdate));
        verify(filmStorage, never()).rateByFilmId(1L);
        assertEquals("There is no genre with id=1", throwable.getMessage());
    }

    @Test
    void addFilm() {
        FilmDTO dtoToAdd = new FilmDTO();
        Film filmToAdd = FilmMapper.mapToFilm(dtoToAdd);
        when(filmStorage.save(filmToAdd)).thenReturn(new Film().setId(1L));

        FilmDTO actualFilm = service.addFilm(dtoToAdd);

        verify(filmStorage, times(1)).save(filmToAdd);
        verify(filmStorage, times(1)).updateGenres(null, 1L);
        assertSame(dtoToAdd, actualFilm);
        assertEquals(1L, actualFilm.getId());
    }

    @Test
    void addFilmLike_whenFilmIsNotExist() {
        long filmId = 2L;
        long userId = 10L;
        when(filmStorage.existById(filmId)).thenReturn(false);
        when(userStorage.existById(userId)).thenReturn(true);

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.addFilmLike(filmId, userId));
        verify(userStorage, times(1)).existById(userId);
        verify(filmStorage, times(1)).existById(filmId);
        assertEquals("There is no film with id=" + filmId, throwable.getMessage());
    }

    @Test
    void addFilmLike_whenUserIsNotExist() {
        long filmId = 2L;
        long userId = 10L;
        when(userStorage.existById(userId)).thenReturn(false);

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.addFilmLike(filmId, userId));
        verify(userStorage, times(1)).existById(userId);
        verify(filmStorage, never()).existById(filmId);
        assertEquals("There is no user with id=" + userId, throwable.getMessage());
    }

    @Test
    void addFilmLike_whenLikeIsAlreadySet() {
        long filmId = 2L;
        long userId = 10L;

        when(filmStorage.existById(filmId)).thenReturn(true);
        when(userStorage.existById(userId)).thenReturn(true);
        when(filmStorage.addLike(filmId, userId)).thenReturn(false);

        assertFalse(service.addFilmLike(filmId, userId));

        verify(userStorage, times(1)).existById(userId);
        verify(filmStorage, times(1)).existById(filmId);
    }

    @Test
    void addFilmLike_whenLikeIsNotSet() {
        long filmId = 2L;
        long userId = 10L;

        when(filmStorage.existById(filmId)).thenReturn(true);
        when(userStorage.existById(userId)).thenReturn(true);
        when(filmStorage.addLike(filmId, userId)).thenReturn(true);

        assertTrue(service.addFilmLike(filmId, userId));

        verify(userStorage, times(1)).existById(userId);
        verify(filmStorage, times(1)).existById(filmId);
    }

    @Test
    void deleteFilmLike_whenFilmIsNotExist() {
        long filmId = 2L;
        long userId = 10L;
        when(filmStorage.existById(filmId)).thenReturn(false);
        when(userStorage.existById(userId)).thenReturn(true);

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.deleteFilmLike(filmId, userId));
        verify(userStorage, times(1)).existById(userId);
        verify(filmStorage, times(1)).existById(filmId);
        assertEquals("There is no film with id=" + filmId, throwable.getMessage());
    }

    @Test
    void deleteFilmLike_whenUserIsNotExist() {
        long filmId = 2L;
        long userId = 10L;
        when(userStorage.existById(userId)).thenReturn(false);

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.deleteFilmLike(filmId, userId));
        verify(userStorage, times(1)).existById(userId);
        verify(filmStorage, never()).existById(filmId);
        assertEquals("There is no user with id=" + userId, throwable.getMessage());
    }

    @Test
    void deleteFilmLike_whenLikeIsAlreadySet() {
        long filmId = 2L;
        long userId = 10L;

        when(filmStorage.existById(filmId)).thenReturn(true);
        when(userStorage.existById(userId)).thenReturn(true);
        when(filmStorage.removeLike(filmId, userId)).thenReturn(true);

        assertTrue(service.deleteFilmLike(filmId, userId));

        verify(userStorage, times(1)).existById(userId);
        verify(filmStorage, times(1)).existById(filmId);
        verify(filmStorage, times(1)).removeLike(filmId, userId);

    }

    @Test
    void deleteFilmLike_whenLikeIsNotSet() {
        long filmId = 2L;
        long userId = 10L;

        when(filmStorage.existById(filmId)).thenReturn(true);
        when(userStorage.existById(userId)).thenReturn(true);
        when(filmStorage.removeLike(filmId, userId)).thenReturn(false);

        assertFalse(service.deleteFilmLike(filmId, userId));

        verify(userStorage, times(1)).existById(userId);
        verify(filmStorage, times(1)).existById(filmId);
        verify(filmStorage, times(1)).removeLike(filmId, userId);

    }

    @Test
    void getPopularFilms() {
        int count = 2;
        List<FilmDTO> expectedFilms = List.of(new FilmDTO().setRate(3).setId(3L).setGenres(Collections.emptyList()),
                new FilmDTO().setRate(2).setGenres(Collections.emptyList()));
        List<Film> returnFilms = List.of(new Film().setId(1L),
                new Film().setId(2L),
                new Film().setId(3L));
        when(filmStorage.findAll()).thenReturn(returnFilms);
        when(filmStorage.rateByFilmId(1L)).thenReturn(1);
        when(filmStorage.rateByFilmId(2L)).thenReturn(2);
        when(filmStorage.rateByFilmId(3L)).thenReturn(3);

        List<FilmDTO> popularFilms = service.getPopularFilms(count);

        verify(filmStorage, times(1)).findAll();
        verify(filmStorage, times(1)).findGenresByFilmId(1L);
        verify(filmStorage, times(1)).findGenresByFilmId(2L);
        verify(filmStorage, times(1)).findGenresByFilmId(3L);
        verify(filmStorage, times(1)).rateByFilmId(1L);
        verify(filmStorage, times(1)).rateByFilmId(2L);
        verify(filmStorage, times(1)).rateByFilmId(3L);

        assertEquals(count, popularFilms.size());
        assertEquals(expectedFilms.getFirst(), popularFilms.getFirst());
        assertEquals(expectedFilms.getLast(), popularFilms.getLast());
    }

    @Test
    void getFilmById_whenFilmIsNotFound() {
        when(filmStorage.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.getFilmById(1L));

        assertEquals("There is no film with id=1", throwable.getMessage());
    }

    @Test
    void getFilmById_whenFilmIsFound() {
        Film film = new Film().setId(1L)
                .setName("test");
        FilmDTO expectedFilm = new FilmDTO()
                .setId(1L)
                .setName("test")
                .setGenres(Collections.emptyList())
                .setRate(8);
        when(filmStorage.findById(1L)).thenReturn(Optional.ofNullable(film));
        when(filmStorage.findGenresByFilmId(1L)).thenReturn(Collections.emptyList());
        when(filmStorage.rateByFilmId(1L)).thenReturn(8);

        FilmDTO actualFilm = service.getFilmById(1L);

        verify(filmStorage, times(1)).findById(1L);
        verify(filmStorage, times(1)).findGenresByFilmId(1L);
        verify(filmStorage, times(1)).rateByFilmId(1L);
        assertEquals(expectedFilm, actualFilm);
    }
}