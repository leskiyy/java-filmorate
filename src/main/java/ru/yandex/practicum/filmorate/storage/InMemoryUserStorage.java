package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private static final AtomicLong id = new AtomicLong(0L);
    private static final Map<Long, User> users = new ConcurrentHashMap<>();
    private static final Map<Long, Set<Long>> friends = new ConcurrentHashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(nextId());
        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("There is no user with id=" + user.getId());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Set<Long> getFriendsIdsByUserId(long id) {
        return friends.get(id);
    }

    private long nextId() {
        return id.incrementAndGet();
    }

}