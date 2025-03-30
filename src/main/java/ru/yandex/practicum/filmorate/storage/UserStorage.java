package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> findAll();

    User save(User user);

    User update(User user);

    Optional<User> findById(long id);

    boolean deleteById(long id);

    boolean existById(long id);

    boolean addFriendshipRow(long id, long friendId);

    boolean deleteFriendshipRow(long id, long friendId);

    List<User> getFriendsByUserId(long id);
}