package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MpaServiceTest {

    @Mock
    MpaRepository repository;

    @InjectMocks
    MpaService service;

    @Test
    void getAllMpa() {
        List<Mpa> expected = List.of(new Mpa());
        when(repository.findAll()).thenReturn(expected);

        List<Mpa> actual = service.getAllMpa();

        verify(repository, times(1)).findAll();
        assertSame(expected, actual);
        assertSame(expected.getFirst(), actual.getFirst());
    }

    @Test
    void getMpaById_whenMpaIsFound() {
        Mpa expected = new Mpa();
        when(repository.findById(1)).thenReturn(Optional.of(expected));

        Mpa actual = service.getMpaById(1);

        verify(repository, times(1)).findById(1);
        assertSame(expected, actual);
    }

    @Test
    void getMpaById_whenMpaIsNotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.getMpaById(1));
        assertEquals("There is no mpa with id=1", throwable.getMessage());
    }
}