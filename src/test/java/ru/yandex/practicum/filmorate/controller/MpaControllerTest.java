package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MpaControllerTest {

    @Mock
    MpaService service;

    @InjectMocks
    MpaController controller;

    @Test
    void getAllMpa() {
        Mpa expectedMpa = new Mpa();
        when(service.getAllMpa()).thenReturn(List.of(expectedMpa));

        List<Mpa> actualMpa = controller.getAllMpa();

        verify(service, times(1)).getAllMpa();
        assertEquals(1, actualMpa.size());
        assertEquals(expectedMpa, actualMpa.getFirst());
    }

    @Test
    void getMpaById_whenMpaIsFound() {
        Mpa expectedMpa = new Mpa();
        when(service.getMpaById(1)).thenReturn(expectedMpa);

        Mpa actualMpa = controller.getMpaById(1);

        verify(service, times(1)).getMpaById(1);
        assertEquals(expectedMpa, actualMpa);
    }

    @Test
    void getMpaById_whenMpaIsNotFound() {
        when(service.getMpaById(1)).thenThrow(new NotFoundException("test"));

        Throwable throwable = assertThrows(NotFoundException.class, () -> controller.getMpaById(1));

        verify(service, times(1)).getMpaById(1);
        assertEquals("test", throwable.getMessage());
    }
}