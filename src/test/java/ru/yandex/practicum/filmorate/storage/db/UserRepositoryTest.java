package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class, UserRowMapper.class})
class UserRepositoryTest {

    private final UserStorage storage;
    private final JdbcTemplate jdbc;

    @Test
    void findAll() {
        List<User> users = storage.findAll();
        assertEquals(2, users.size());
    }

    @Test
    void save() {
        clearTables();
        User user = new User()
                .setId(null)
                .setEmail("l@l.l")
                .setLogin("login unique")
                .setName("name")
                .setBirthday(LocalDate.of(2000, 1, 1));

        User save = storage.save(user);
        assertNotNull(save.getId());

        Map<String, Object> dbUser = jdbc.queryForMap(
                "SELECT * FROM USERS WHERE USER_ID = ?",
                save.getId()
        );

        assertThat(dbUser.get("EMAIL")).isEqualTo("l@l.l");
        assertThat(dbUser.get("NAME")).isEqualTo("name");
        assertThat(dbUser.get("LOGIN")).isEqualTo("login unique");
        assertThat(dbUser.get("BIRTHDAY")).isEqualTo(java.sql.Date.valueOf("2000-01-01"));
    }

    @Test
    void deleteById_whenUserExists() {
        boolean deleted = storage.deleteById(1L);

        assertThat(deleted).isTrue();
        assertThatThrownBy(() -> jdbc.queryForMap("SELECT * FROM USERS WHERE USER_ID = 1"))
                .isInstanceOf(EmptyResultDataAccessException.class);

    }

    @Test
    void deleteById_whenUserNotExists() {
        boolean deleted = storage.deleteById(4L);
        assertThat(deleted).isFalse();
    }

    @Test
    void update_whenUserExists() {
        User userToUpdate = new User()
                .setId(1L)
                .setEmail("udpated@mail.q")
                .setName("update name")
                .setLogin("updated login")
                .setBirthday(LocalDate.of(2020, 2, 2));

        User updated = storage.update(userToUpdate);
        assertThat(updated.getId()).isEqualTo(1L);

        Map<String, Object> dbUser = jdbc.queryForMap(
                "SELECT * FROM USERS WHERE USER_ID = ?",
                updated.getId()
        );

        assertThat(dbUser.get("EMAIL")).isEqualTo("udpated@mail.q");
        assertThat(dbUser.get("NAME")).isEqualTo("update name");
        assertThat(dbUser.get("LOGIN")).isEqualTo("updated login");
        assertThat(dbUser.get("BIRTHDAY")).isEqualTo(java.sql.Date.valueOf("2020-02-02"));
    }

    @Test
    void update_whenUserNotExists() {
        User userToUpdate = new User()
                .setId(6L)
                .setEmail("udpated@mail.q")
                .setName("update name")
                .setLogin("updated login")
                .setBirthday(LocalDate.of(2020, 2, 2));

        assertThatThrownBy(() -> storage.update(userToUpdate)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_whenUserExists() {
        Optional<User> byId = storage.findById(1);

        assertThat(byId).isPresent();

        assertThat(byId).isPresent().hasValueSatisfying(user -> {
            assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
            assertThat(user).hasFieldOrPropertyWithValue("email", "email");
            assertThat(user).hasFieldOrPropertyWithValue("login", "login");
            assertThat(user).hasFieldOrPropertyWithValue("name", "name");
            assertThat(user).hasFieldOrPropertyWithValue("birthday",
                    LocalDate.of(2000, 1, 1));
        });
    }

    @Test
    void findById_whenUserNowExists() {
        Optional<User> byId = storage.findById(6);
        assertThat(byId).isEmpty();
    }

    @Test
    void existById_whenTrue() {
        boolean existById = storage.existById(1);
        assertThat(existById).isTrue();
    }

    @Test
    void existById_whenFalse() {
        boolean existById = storage.existById(6);
        assertThat(existById).isFalse();
    }

    @Test
    void addFriendshipRow_whenFriendshipIsSet() {
        addFriendshipRow(1, 2);

        boolean addFriendshipRow = storage.addFriendshipRow(1, 2);
        assertThat(addFriendshipRow).isFalse();
    }

    @Test
    void addFriendshipRow_whenFriendshipIsNotSet() {
        boolean addFriendshipRow = storage.addFriendshipRow(1, 2);
        assertThat(addFriendshipRow).isTrue();
    }

    @Test
    void deleteFriendshipRow__whenFriendshipIsSet() {
        addFriendshipRow(1, 2);

        boolean deleteFriendshipRow = storage.deleteFriendshipRow(1, 2);
        assertThat(deleteFriendshipRow).isTrue();
    }

    @Test
    void deleteFriendshipRow__whenFriendshipIsNotSet() {
        boolean deleteFriendshipRow = storage.deleteFriendshipRow(1, 2);
        assertThat(deleteFriendshipRow).isFalse();
    }

    @Test
    void getFriendsByUserId__whenFriendshipIsNotSet() {
        addFriendshipRow(1, 2);

        List<User> friendsByUserId = storage.getFriendsByUserId(1L);
        assertThat(friendsByUserId).hasSize(1);
        assertThat(friendsByUserId.getFirst()).hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "name2")
                .hasFieldOrPropertyWithValue("login", "login2")
                .hasFieldOrPropertyWithValue("email", "email2")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 1, 2));
    }

    @BeforeEach
    void initDb() {
        jdbc.update("INSERT INTO PUBLIC.USERS (USER_ID,EMAIL,LOGIN,NAME,BIRTHDAY)\n" +
                    "\tVALUES (1,'email','login','name','2000-01-01')");
        jdbc.update("INSERT INTO PUBLIC.USERS (USER_ID,EMAIL,LOGIN,NAME,BIRTHDAY)\n" +
                    "\tVALUES (2,'email2','login2','name2','2000-01-02')");
    }

    @AfterEach
    void clearTables() {
        jdbc.update("DELETE FROM FRIENDS");
        jdbc.update("DELETE FROM USERS");
    }

    private void addFriendshipRow(long userId, long friendId) {
        jdbc.update("INSERT INTO FRIENDS(USER_ID, FRIEND_ID) VALUES (?,?)", userId, friendId);
    }

}