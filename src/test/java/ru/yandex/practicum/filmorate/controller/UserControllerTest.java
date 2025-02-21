package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    @Test
    void getUsers() {
        User expectedUser = new User();
        when(service.getAllUsers()).thenReturn(List.of(expectedUser));

        List<User> actualUsers = service.getAllUsers();

        verify(service, times(1)).getAllUsers();
        assertEquals(1, actualUsers.size());
        assertEquals(expectedUser, actualUsers.getFirst());
    }

    @Test
    void addUser() {
        User expectedUser = new User();
        User userToAdd = new User();
        when(service.addUser(userToAdd)).thenReturn(expectedUser);

        User actualUser = service.addUser(userToAdd);

        verify(service, times(1)).addUser(userToAdd);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void updateUser_whenUserIsFound() {
        User expectedUser = new User();
        User userToUpdate = new User();
        when(service.updateUser(userToUpdate)).thenReturn(expectedUser);

        User actualUser = service.updateUser(userToUpdate);

        verify(service, times(1)).updateUser(userToUpdate);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void updateUser_whenUserIsNotFound() {
        User userToUpdate = new User();
        when(service.updateUser(userToUpdate)).thenThrow(new NotFoundException("test"));

        Throwable throwable = assertThrows(NotFoundException.class,
                () -> service.updateUser(userToUpdate));

        verify(service, times(1)).updateUser(userToUpdate);

        assertEquals("test", throwable.getMessage());
    }

    @Test
    void addFriend_whenTrue() {
        when(service.addFriend(1, 2)).thenReturn(true);

        Map<String, String> response = controller.addFriend(1, 2);

        verify(service, times(1)).addFriend(1, 2);
        assertEquals("Users id=1 and id=2 have become friends", response.get("SUCCESS"));
    }

    @Test
    void addFriend_whenFalse() {
        when(service.addFriend(1, 2)).thenReturn(false);

        Map<String, String> response = controller.addFriend(1, 2);
        System.out.println(response);

        verify(service, times(1)).addFriend(1, 2);
        assertEquals("Users id=1 and id=2 are friends already", response.get("FAIL"));
    }

    @Test
    void deleteFriend_whenTrue() {
        when(service.deleteFriend(1, 2)).thenReturn(true);

        Map<String, String> response = controller.deleteFriend(1, 2);

        verify(service, times(1)).deleteFriend(1, 2);
        assertEquals("Users id=1 and id=2 are not friends anymore", response.get("SUCCESS"));
    }

    @Test
    void deleteFriend_whenFalse() {
        when(service.deleteFriend(1, 2)).thenReturn(false);

        Map<String, String> response = controller.deleteFriend(1, 2);

        verify(service, times(1)).deleteFriend(1, 2);
        assertEquals("Users id=1 and id=2 are not friends", response.get("FAIL"));
    }

    @Test
    void getFriendsByUserId() {
        User friend1 = new User();
        User friend2 = new User();
        when(service.getFriendsByUserId(1)).thenReturn(List.of(friend1, friend2));

        List<User> actualFriends = controller.getFriendsByUserId(1);

        verify(service, times(1)).getFriendsByUserId(1);
        assertSame(friend1, actualFriends.getFirst());
        assertSame(friend2, actualFriends.getLast());
    }

    @Test
    void getCommonFriends() {
        User commonFriend1 = new User();
        User commonFriend2 = new User();
        when(service.getCommonFriends(1, 2)).thenReturn(List.of(commonFriend1, commonFriend2));

        List<User> actualCommonFriends = controller.getCommonFriends(1, 2);

        verify(service, times(1)).getCommonFriends(1, 2);
        assertSame(commonFriend1, actualCommonFriends.getFirst());
        assertSame(commonFriend2, actualCommonFriends.getLast());
    }

}