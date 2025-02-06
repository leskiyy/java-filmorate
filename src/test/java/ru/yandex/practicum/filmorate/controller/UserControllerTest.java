package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserController userController;

    private static final User user = new User(12L, "email1", "login1",
            "name1", LocalDate.of(2010, 1, 1));
    private static final User expectedUser = new User(0L, "email", "login",
            "name", LocalDate.of(2000, 1, 1));

    @Test
    void getUsers() {
        List<User> expectedUsers = List.of(expectedUser);
        when(userStorage.getAllUsers()).thenReturn(expectedUsers);

        List<User> actualUsers = userController.getUsers();

        verify(userStorage, times(1)).getAllUsers();
        assertEquals(expectedUsers.size(), actualUsers.size());
        assertEquals(expectedUsers.getFirst(), actualUsers.getFirst());
    }

    @Test
    void addUser() {
        when(userStorage.addUser(user)).thenReturn(expectedUser);

        User actualUser = userController.addUser(user);

        verify(userStorage, times(1)).addUser(user);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void updateUser_whenUserIsFound() {
        when(userStorage.updateUser(user)).thenReturn(expectedUser);

        User actualUser = userController.updateUser(user);

        verify(userStorage, times(1)).updateUser(user);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void updateUser_whenUserIsNotFound() {
        when(userStorage.updateUser(user)).thenThrow(new NotFoundException("test"));

        Throwable throwable = assertThrows(ResponseStatusException.class,
                () -> userController.updateUser(user));

        verify(userStorage, times(1)).updateUser(user);

        assertEquals("404 NOT_FOUND \"test\"", throwable.getMessage());
    }
}