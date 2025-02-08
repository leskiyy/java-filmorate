package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserStorage {

    private static Long id = 0L;
    private final Map<Long, User> users = new HashMap<>();

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User addUser(User user) {
        validateName(user);
        user.setId(nextId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("There is no user with id=" + user.getId());
        }
        validateName(user);
        users.put(user.getId(), user);
        return user;
    }

    private long nextId() {
        return ++id;
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
