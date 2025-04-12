package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    public List<User> findAll() {
        String findAllQuery = "SELECT * FROM USERS";
        return jdbc.query(findAllQuery, mapper);
    }

    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertQuery = "INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?,?,?,?)";

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    public boolean deleteById(long id) {
        jdbc.update("MERGE INTO DELETED_USER_IDS(DELETED_USER_ID) VALUES (?)", id);

        String deleteById = "DELETE FROM USERS WHERE USER_ID = ?";
        int updatedRows = jdbc.update(deleteById, id);
        return updatedRows > 0;
    }

    public User update(User user) {
        Long id = user.getId();
        if (!existById(id)) throw new NotFoundException("There is no user with id=" + id);

        String updateQuery = """
                UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ?
                WHERE USER_ID = ?""";
        jdbc.update(updateQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                id);
        return user;
    }

    public Optional<User> findById(long id) {
        try {
            String findByIdQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
            User user = jdbc.queryForObject(findByIdQuery, mapper, id);
            return Optional.ofNullable(user);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existById(long id) {
        String isExistByIdQuery = "SELECT EXISTS(SELECT 1 FROM USERS WHERE USER_ID = ?)";
        return jdbc.queryForObject(isExistByIdQuery, Boolean.class, id);
    }

    public boolean existDeletedUserById(long id) {
        String query = "SELECT EXISTS(SELECT 1 FROM DELETED_USER_IDS WHERE DELETED_USER_ID = ?)";
        return jdbc.queryForObject(query, Boolean.class, id);
    }

    public boolean addFriendshipRow(long id, long friendId) {
        String addFriendshipRowQuery = """
                INSERT INTO FRIENDS(USER_ID, FRIEND_ID)
                VALUES (?,?)""";
        try {
            jdbc.update(addFriendshipRowQuery, id, friendId);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean deleteFriendshipRow(long id, long friendId) {
        String deleteFriendshipQuery = "DELETE FROM FRIENDS WHERE USER_ID=? AND FRIEND_ID=?";
        int deletedRows = jdbc.update(deleteFriendshipQuery, id, friendId);
        return deletedRows > 0;
    }

    public List<User> getFriendsByUserId(long id) {
        String findFriendsByUserIdQuery = """
                SELECT * FROM USERS WHERE USER_ID IN
                    (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)""";
        return jdbc.query(findFriendsByUserIdQuery, mapper, id);
    }
}
