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

    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?,?,?,?)";
    private static final String UPDATE_QUERY = """
            UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?""";
    private static final String IS_EXIST_BY_ID_QUERY = "SELECT EXISTS(SELECT 1 FROM USERS WHERE USER_ID = ?)";
    private static final String ADD_FRIENDSHIP_ROW_QUERY = """
            INSERT INTO FRIENDS(USER_ID, FRIEND_ID) VALUES (?,?)""";
    private static final String DELETE_FRIENDSHIP_QUERY = """
            DELETE FROM FRIENDS WHERE USER_ID=? AND FRIEND_ID=?""";
    private static final String FIND_FRIENDS_BY_USER_ID_QUERY = """
            SELECT * FROM USERS WHERE USER_ID IN (
                SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)""";
    private static final String DELETE_BY_ID = "DELETE FROM USERS WHERE USER_ID = ?";

    public List<User> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
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
        int updatedRows = jdbc.update(DELETE_BY_ID, id);
        return updatedRows > 0;
    }

    public User update(User user) {
        Long id = user.getId();
        if (!existById(id)) throw new NotFoundException("There is no user with id=" + id);

        jdbc.update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                id);
        return user;
    }

    public Optional<User> findById(long id) {
        try {
            User user = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(user);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existById(long id) {
        return jdbc.queryForObject(IS_EXIST_BY_ID_QUERY, Boolean.class, id);
    }

    public boolean addFriendshipRow(long id, long friendId) {
        try {
            jdbc.update(ADD_FRIENDSHIP_ROW_QUERY, id, friendId);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean deleteFriendshipRow(long id, long friendId) {
        int deletedRows = jdbc.update(DELETE_FRIENDSHIP_QUERY, id, friendId);
        return deletedRows > 0;
    }

    public List<User> getFriendsByUserId(long id) {
        return jdbc.query(FIND_FRIENDS_BY_USER_ID_QUERY, mapper, id);
    }
}
