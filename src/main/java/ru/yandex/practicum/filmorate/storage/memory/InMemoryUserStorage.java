package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final AtomicLong id = new AtomicLong(0L);
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> friends = new ConcurrentHashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        user.setId(nextId());
        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public boolean deleteById(long id) {
        friends.remove(id);
        friends.forEach((k, v) -> v.remove(id));
        return users.remove(id, users.get(id));
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("There is no user with id=" + user.getId());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean existById(long id) {
        return users.get(id) != null;
    }

    @Override
    public boolean addFriendshipRow(long id, long friendId) {
        return friends.get(id).add(friendId);
    }

    @Override
    public boolean deleteFriendshipRow(long id, long friendId) {
        return friends.get(id).remove(friendId);
    }

    @Override
    public List<User> getFriendsByUserId(long id) {
        Set<Long> friendsIds = friends.get(id);
        return users.values().stream().filter(el -> friendsIds.contains(el.getId())).toList();
    }

    private long nextId() {
        return id.incrementAndGet();
    }

}