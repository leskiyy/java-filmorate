package ru.yandex.practicum.filmorate.storage.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {

    private static UserStorage storage;

    @BeforeEach
    void init() {
        storage = new InMemoryUserStorage();
    }

    @Test
    void findAll() {
        addTreeUsers();
        List<User> users = storage.findAll();

        assertEquals(3, users.size());
        assertEquals("name1", users.getFirst().getName());
        assertEquals("name3", users.getLast().getName());
    }

    @Test
    void save() {
        User user = new User();
        User save = storage.save(user);
        assertSame(user, save);
        assertEquals(1, save.getId());
        assertEquals(1, storage.findAll().size());
    }

    @Test
    void deleteById_whenUserExists() {
        addTreeUsers();
        boolean deleted = storage.deleteById(2);

        assertTrue(deleted);
        assertEquals(2, storage.findAll().size());
        assertEquals(0, storage.getFriendsByUserId(1).size());
    }

    @Test
    void deleteById_whenUserNotExists() {
        addTreeUsers();
        boolean deleted = storage.deleteById(4);

        assertFalse(deleted);
        assertEquals(3, storage.findAll().size());
    }

    @Test
    void update_whenUserExists() {
        addTreeUsers();
        User userToUpdate = new User()
                .setId(1L)
                .setName("updatedName")
                .setEmail("new@mail.a");

        User updated = storage.update(userToUpdate);

        assertSame(userToUpdate, updated);
        assertSame(userToUpdate, storage.findAll().getFirst());
        assertEquals("updatedName", updated.getName());
        assertEquals("new@mail.a", updated.getEmail());
    }

    @Test
    void update_whenUserNotExists() {
        addTreeUsers();
        User userToUpdate = new User()
                .setId(5L)
                .setName("updatedName")
                .setEmail("new@mail.a");

        Throwable throwable = assertThrows(NotFoundException.class, () -> storage.update(userToUpdate));
        assertEquals("There is no user with id=5", throwable.getMessage());
    }

    @Test
    void findById_whenUserExists() {
        addTreeUsers();

        Optional<User> byId = storage.findById(2);

        assertTrue(byId.isPresent());
        assertEquals(2L, byId.get().getId());
    }

    @Test
    void findById_whenUserNotExists() {
        addTreeUsers();

        Optional<User> byId = storage.findById(6);

        assertTrue(byId.isEmpty());
    }

    @Test
    void existById_whenUserExists() {
        addTreeUsers();

        boolean existById = storage.existById(3L);
        assertTrue(existById);
    }

    @Test
    void existById_whenUserNotExists() {
        addTreeUsers();

        boolean existById = storage.existById(4L);
        assertFalse(existById);
    }

    @Test
    void addFriendshipRow_whenRowExist() {
        addTreeUsers();

        boolean addFriendshipRow = storage.addFriendshipRow(2, 1);
        assertFalse(addFriendshipRow);
    }

    @Test
    void addFriendshipRow_whenRowNotExist() {
        addTreeUsers();

        boolean addFriendshipRow = storage.addFriendshipRow(1, 3);
        assertTrue(addFriendshipRow);
    }

    @Test
    void deleteFriendshipRow_whenRowExist() {
        addTreeUsers();

        boolean addFriendshipRow = storage.deleteFriendshipRow(1, 2);
        assertTrue(addFriendshipRow);
    }

    @Test
    void deleteFriendshipRow_whenRowNotExist() {
        addTreeUsers();

        boolean addFriendshipRow = storage.deleteFriendshipRow(1, 3);
        assertFalse(addFriendshipRow);
    }

    @Test
    void getFriendsByUserId() {
        addTreeUsers();
        List<User> users = storage.findAll();
        List<User> friendsByUserId1 = storage.getFriendsByUserId(1);
        List<User> friendsByUserId2 = storage.getFriendsByUserId(2);
        List<User> friendsByUserId3 = storage.getFriendsByUserId(3);

        assertEquals(1, friendsByUserId1.size());
        assertSame(users.get(1), friendsByUserId1.getFirst());

        assertEquals(2, friendsByUserId2.size());
        assertSame(users.getFirst(), friendsByUserId2.getFirst());
        assertSame(users.getLast(), friendsByUserId2.getLast());

        assertEquals(0, friendsByUserId3.size());
    }

    private void addTreeUsers() {
        storage.save(new User().setEmail("a1@a.a")
                .setLogin("login1")
                .setName("name1")
                .setBirthday(LocalDate.of(1991, 1, 1)));
        storage.save(new User()
                .setEmail("a2@a.a")
                .setLogin("login2")
                .setName("name2")
                .setBirthday(LocalDate.of(1992, 1, 1)));
        storage.save(new User()
                .setEmail("a3@a.a")
                .setLogin("login3")
                .setName("name3")
                .setBirthday(LocalDate.of(1993, 1, 1)));

        storage.addFriendshipRow(1, 2);
        storage.addFriendshipRow(2, 1);
        storage.addFriendshipRow(2, 3);
    }
}