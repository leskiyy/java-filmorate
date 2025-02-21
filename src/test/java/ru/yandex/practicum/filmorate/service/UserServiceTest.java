package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserStorage storage;

    @InjectMocks
    UserService service;

    @Test
    void getAllUsers() {
        User expectedUser = new User();
        List<User> expectedUsers = List.of(expectedUser);
        when(storage.getAllUsers()).thenReturn(expectedUsers);

        List<User> actualUsers = service.getAllUsers();

        verify(storage, times(1)).getAllUsers();
        assertEquals(expectedUsers.size(), actualUsers.size());
        assertSame(expectedUsers.getFirst(), actualUsers.getLast());
    }

    @Test
    void updateUser() {
        User userToUpdate = new User();
        userToUpdate.setLogin("name");
        User expectedUser = new User();
        when(storage.updateUser(userToUpdate)).thenReturn(expectedUser);

        User actualUser = service.updateUser(userToUpdate);

        verify(storage, times(1)).updateUser(userToUpdate);
        assertSame(expectedUser, actualUser);
        assertEquals("name", userToUpdate.getName());
    }

    @Test
    void addUser() {
        User userToAdd = new User();
        userToAdd.setLogin("name");
        User expectedUser = new User();
        when(storage.addUser(userToAdd)).thenReturn(expectedUser);

        User actualUser = service.addUser(userToAdd);

        verify(storage, times(1)).addUser(userToAdd);
        assertSame(expectedUser, actualUser);
        assertEquals("name", userToAdd.getName());
    }

    @Test
    void addFriend_whenUsersAreNotFriends() {
        HashSet<Long> friends1 = new HashSet<>(Set.of(2L, 3L, 4L));
        HashSet<Long> friends2 = new HashSet<>(Set.of(2L, 3L, 4L));
        when(storage.getFriendsIdsByUserId(1L)).thenReturn(friends1);
        when(storage.getFriendsIdsByUserId(5L)).thenReturn(friends2);

        assertTrue(service.addFriend(1L, 5L));

        verify(storage, times(1)).getFriendsIdsByUserId(1L);
        verify(storage, times(1)).getFriendsIdsByUserId(5L);
        assertTrue(friends1.contains(5L));
        assertTrue(friends2.contains(1L));
    }

    @Test
    void addFriend_whenUsersAreFriends() {
        HashSet<Long> friends1 = new HashSet<>(Set.of(2L, 3L, 4L, 5L));
        HashSet<Long> friends2 = new HashSet<>(Set.of(2L, 3L, 4L, 1L));
        when(storage.getFriendsIdsByUserId(1L)).thenReturn(friends1);

        assertFalse(service.addFriend(1L, 5L));

        verify(storage, times(1)).getFriendsIdsByUserId(1L);
        verify(storage, times(1)).getFriendsIdsByUserId(5L);
        assertTrue(friends1.contains(5L));
        assertTrue(friends2.contains(1L));
    }

    @Test
    void addFriend_whenUserDoesNotExist() {
        when(storage.getFriendsIdsByUserId(1L)).thenReturn(null);

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.addFriend(1L, 5L));

        verify(storage, times(1)).getFriendsIdsByUserId(1L);
        assertEquals("There is no user with id=1", throwable.getMessage());
    }

    @Test
    void addFriend_whenFriendDoesNotExist() {
        HashSet<Long> friends1 = new HashSet<>(Set.of(2L, 3L, 4L, 5L));
        when(storage.getFriendsIdsByUserId(1L)).thenReturn(friends1);
        when(storage.getFriendsIdsByUserId(5L)).thenReturn(null);

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.addFriend(1L, 5L));

        verify(storage, times(1)).getFriendsIdsByUserId(1L);
        verify(storage, times(1)).getFriendsIdsByUserId(5L);
        assertEquals("There is no user with id=5", throwable.getMessage());
    }

    @Test
    void addFriend_withSameIds() {
        Throwable throwable = assertThrows(ValidationException.class, () -> service.addFriend(1L, 1L));

        assertEquals("Can't add yourself as a friend", throwable.getMessage());
    }

    @Test
    void deleteFriend_whenFriendUsersAreFriends() {
        HashSet<Long> friends1 = new HashSet<>(Set.of(2L, 3L, 4L, 5L));
        HashSet<Long> friends2 = new HashSet<>(Set.of(2L, 3L, 4L, 1L));
        when(storage.getFriendsIdsByUserId(1L)).thenReturn(friends1);
        when(storage.getFriendsIdsByUserId(5L)).thenReturn(friends2);

        assertTrue(service.deleteFriend(1L, 5L));

        verify(storage, times(1)).getFriendsIdsByUserId(1L);
        verify(storage, times(1)).getFriendsIdsByUserId(5L);
        assertFalse(friends1.contains(5L));
        assertFalse(friends2.contains(1L));
    }

    @Test
    void deleteFriend_whenFriendUsersAreNotFriends() {
        HashSet<Long> friends1 = new HashSet<>(Set.of(2L, 3L, 4L));
        HashSet<Long> friends2 = new HashSet<>(Set.of(2L, 3L, 4L));
        when(storage.getFriendsIdsByUserId(1L)).thenReturn(friends1);
        when(storage.getFriendsIdsByUserId(5L)).thenReturn(friends2);

        assertFalse(service.deleteFriend(1L, 5L));

        verify(storage, times(1)).getFriendsIdsByUserId(1L);
        verify(storage, times(1)).getFriendsIdsByUserId(5L);
        assertFalse(friends1.contains(5L));
        assertFalse(friends2.contains(1L));
    }

    @Test
    void deleteFriend_whenUserDoesNotExist() {
        when(storage.getFriendsIdsByUserId(1L)).thenReturn(null);

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.deleteFriend(1L, 5L));

        verify(storage, times(1)).getFriendsIdsByUserId(1L);
        assertEquals("There is no user with id=1", throwable.getMessage());
    }

    @Test
    void deleteFriend_whenFriendDoesNotExist() {
        HashSet<Long> friends1 = new HashSet<>(Set.of(2L, 3L, 4L, 5L));
        when(storage.getFriendsIdsByUserId(1L)).thenReturn(friends1);
        when(storage.getFriendsIdsByUserId(5L)).thenReturn(null);

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.deleteFriend(1L, 5L));

        verify(storage, times(1)).getFriendsIdsByUserId(1L);
        verify(storage, times(1)).getFriendsIdsByUserId(5L);
        assertEquals("There is no user with id=5", throwable.getMessage());
    }

    @Test
    void deleteFriend_withSameIds() {
        Throwable throwable = assertThrows(ValidationException.class, () -> service.deleteFriend(1L, 1L));

        assertEquals("Can't delete yourself from friends", throwable.getMessage());
    }

    @Test
    void getFriendsByUserId() {
        List<User> users = initTenUserList();
        List<User> expectedFriends = new ArrayList<>(List.of(users.get(1), users.get(2)));
        when(storage.getFriendsIdsByUserId(1L)).thenReturn(new HashSet<>(Set.of(2L, 3L)));
        when(storage.getAllUsers()).thenReturn(users);

        List<User> actualFriends = service.getFriendsByUserId(1L);

        verify(storage, times(1)).getFriendsIdsByUserId(1L);
        verify(storage, times(1)).getAllUsers();
        assertEquals(2, actualFriends.size());
        assertSame(expectedFriends.getFirst(), actualFriends.getFirst());
        assertSame(expectedFriends.getLast(), actualFriends.getLast());
    }

    @Test
    void getCommonFriends() {
        List<User> users = initTenUserList();
        List<User> expectedCommonFriends = new ArrayList<>(List.of(users.get(4), users.get(5)));
        System.out.println(users.get(5));
        System.out.println(users.get(6));
        when(storage.getFriendsIdsByUserId(1L)).thenReturn(new HashSet<>(Set.of(2L, 3L, 4L, 5L, 6L)));
        when(storage.getFriendsIdsByUserId(10L)).thenReturn(new HashSet<>(Set.of(9L, 8L, 7L, 6L, 5L)));
        when(storage.getAllUsers()).thenReturn(users);

        List<User> actualCommonFriends = service.getCommonFriends(1L, 10L);

        verify(storage, times(1)).getFriendsIdsByUserId(1L);
        verify(storage, times(1)).getFriendsIdsByUserId(10L);
        verify(storage, times(1)).getAllUsers();
        assertEquals(2, actualCommonFriends.size());
        assertSame(expectedCommonFriends.getFirst(), actualCommonFriends.getFirst());
        assertSame(expectedCommonFriends.getLast(), actualCommonFriends.getLast());
    }

    List<User> initTenUserList() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        User user3 = new User();
        user3.setId(3L);
        User user4 = new User();
        user4.setId(4L);
        User user5 = new User();
        user5.setId(5L);
        User user6 = new User();
        user6.setId(6L);
        User user7 = new User();
        user7.setId(7L);
        User user8 = new User();
        user8.setId(8L);
        User user9 = new User();
        user9.setId(9L);
        User user10 = new User();
        user10.setId(10L);
        return new ArrayList<>(List.of(user1, user2, user3, user4, user5, user6, user7, user8, user9, user10));
    }
}