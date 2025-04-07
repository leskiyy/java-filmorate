package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private EventService eventService;


    @InjectMocks
    UserService service;

    @Test
    void getAllUsers() {
        User expectedUser = new User();
        List<User> expectedUsers = List.of(expectedUser);
        when(repository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = service.getAllUsers();

        verify(repository, times(1)).findAll();
        assertEquals(expectedUsers.size(), actualUsers.size());
        assertSame(expectedUsers.getFirst(), actualUsers.getLast());
    }

    @Test
    void updateUser_whenUserIsFound() {
        User userToUpdate = new User()
                .setLogin("login")
                .setId(1L);
        User expectedUser = new User()
                .setLogin("login")
                .setName("login")
                .setId(1L);

        when(repository.update(userToUpdate)).thenReturn(expectedUser);
        when(repository.existById(1L)).thenReturn(true);

        User actualUser = service.updateUser(userToUpdate);

        verify(repository, times(1)).update(userToUpdate);
        verify(repository, times(1)).existById(1L);
        assertSame(expectedUser, actualUser);
    }

    @Test
    void updateUser_whenUserIsNotFound() {
        User userToUpdate = new User()
                .setLogin("login")
                .setId(1L);

        when(repository.existById(1L)).thenReturn(false);

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.updateUser(userToUpdate));

        verify(repository, times(1)).existById(1L);
        verify(repository, never()).update(userToUpdate);
        assertEquals("There is no user with id=1", throwable.getMessage());
    }

    @Test
    void addUser() {
        User userToAdd = new User()
                .setLogin("name");
        User expectedUser = new User()
                .setId(1L)
                .setLogin("name")
                .setName("name");
        when(repository.save(userToAdd)).thenReturn(expectedUser);

        User actualUser = service.addUser(userToAdd);

        verify(repository, times(1)).save(userToAdd);
        assertSame(expectedUser, actualUser);
    }

    @Test
    void addFriend_whenUsersAreNotFriends() {
        when(repository.existById(1L)).thenReturn(true);
        when(repository.existById(5L)).thenReturn(true);
        when(repository.addFriendshipRow(1L, 5L)).thenReturn(true);

        assertTrue(service.addFriend(1L, 5L));

        verify(repository, times(1)).existById(1L);
        verify(repository, times(1)).existById(5L);
        verify(repository, times(1)).addFriendshipRow(1L, 5L);
    }

    //
    @Test
    void addFriend_whenUsersAreFriends() {
        when(repository.existById(1L)).thenReturn(true);
        when(repository.existById(5L)).thenReturn(true);
        when(repository.addFriendshipRow(1L, 5L)).thenReturn(false);

        assertFalse(service.addFriend(1L, 5L));

        verify(repository, times(1)).existById(1L);
        verify(repository, times(1)).existById(5L);
        verify(repository, times(1)).addFriendshipRow(1L, 5L);
    }

    @Test
    void addFriend_whenUserDoesNotExist() {
        when(repository.existById(1L)).thenReturn(false);

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.addFriend(1L, 5L));

        verify(repository, times(1)).existById(1L);
        assertEquals("There is no user with id=1", throwable.getMessage());
    }

    @Test
    void addFriend_whenFriendDoesNotExist() {
        when(repository.existById(1L)).thenReturn(true);
        when(repository.existById(5L)).thenReturn(false);

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.addFriend(1L, 5L));

        verify(repository, times(1)).existById(1L);
        verify(repository, times(1)).existById(5L);
        assertEquals("There is no user with id=5", throwable.getMessage());
    }

    @Test
    void addFriend_withSameIds() {
        Throwable throwable = assertThrows(ValidationException.class, () -> service.addFriend(1L, 1L));

        assertEquals("Can't add yourself as a friend", throwable.getMessage());
    }

    @Test
    void deleteFriend_whenFriendUsersAreFriends() {
        when(repository.existById(1L)).thenReturn(true);
        when(repository.existById(5L)).thenReturn(true);
        when(repository.deleteFriendshipRow(1L, 5L)).thenReturn(true);

        assertTrue(service.deleteFriend(1L, 5L));

        verify(repository, times(1)).existById(1L);
        verify(repository, times(1)).existById(5L);
        verify(repository, times(1)).deleteFriendshipRow(1L, 5L);
    }

    @Test
    void deleteFriend_whenFriendUsersAreNotFriends() {
        when(repository.existById(1L)).thenReturn(true);
        when(repository.existById(5L)).thenReturn(true);
        when(repository.deleteFriendshipRow(1L, 5L)).thenReturn(false);

        assertFalse(service.deleteFriend(1L, 5L));

        verify(repository, times(1)).existById(1L);
        verify(repository, times(1)).existById(5L);
        verify(repository, times(1)).deleteFriendshipRow(1L, 5L);
    }

    @Test
    void deleteFriend_whenUserDoesNotExist() {
        when(repository.existById(1L)).thenReturn(false);

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.deleteFriend(1L, 5L));

        verify(repository, times(1)).existById(1L);
        assertEquals("There is no user with id=1", throwable.getMessage());
    }

    @Test
    void deleteFriend_whenFriendDoesNotExist() {
        when(repository.existById(1L)).thenReturn(true);
        when(repository.existById(5L)).thenReturn(false);

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.deleteFriend(1L, 5L));

        verify(repository, times(1)).existById(1L);
        verify(repository, times(1)).existById(5L);
        assertEquals("There is no user with id=5", throwable.getMessage());
    }

    @Test
    void deleteFriend_withSameIds() {
        Throwable throwable = assertThrows(ValidationException.class, () -> service.deleteFriend(1L, 1L));

        assertEquals("Can't delete yourself from friends", throwable.getMessage());
    }

    @Test
    void getFriendsByUserId() {
        List<User> expectedFriends = List.of(new User());
        when(repository.getFriendsByUserId(1L)).thenReturn(expectedFriends);
        when(repository.existById(1L)).thenReturn(true);

        List<User> actualFriends = service.getFriendsByUserId(1L);

        verify(repository, times(1)).existById(1L);
        verify(repository, times(1)).getFriendsByUserId(1L);
        assertEquals(1, actualFriends.size());
        assertSame(expectedFriends.getFirst(), actualFriends.getFirst());
    }

    @Test
    void getCommonFriends() {
        List<User> friendList1 = List.of(new User().setEmail("1"),
                new User().setEmail("2"),
                new User().setEmail("3"));
        List<User> friendList2 = List.of(new User().setEmail("2"),
                new User().setEmail("3"),
                new User().setEmail("4"));
        List<User> expectedCommonFriends = List.of(friendList1.get(1), friendList1.get(2));

        when(repository.getFriendsByUserId(1L)).thenReturn(friendList1);
        when(repository.getFriendsByUserId(2L)).thenReturn(friendList2);
        when(repository.existById(1L)).thenReturn(true);
        when(repository.existById(2L)).thenReturn(true);

        List<User> actualCommonFriends = service.getCommonFriends(1L, 2L);

        verify(repository, times(2)).existById(1L);
        verify(repository, times(2)).existById(2L);
        verify(repository, times(1)).getFriendsByUserId(1L);
        verify(repository, times(1)).getFriendsByUserId(2L);
        assertEquals(2, actualCommonFriends.size());
        assertSame(expectedCommonFriends.getFirst(), actualCommonFriends.getFirst());
        assertSame(expectedCommonFriends.getLast(), actualCommonFriends.getLast());
    }
}