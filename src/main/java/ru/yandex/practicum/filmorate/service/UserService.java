package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User updateUser(@Valid User user) {
        validateName(user);
        return userStorage.updateUser(user);
    }

    public User addUser(@Valid User user) {
        validateName(user);
        return userStorage.addUser(user);
    }

    public boolean addFriend(@Positive long id, @Positive long friendId) {
        if (id == friendId) {
            throw new ValidationException("Can't add yourself as a friend");
        }
        Set<Long> friends1 = getFriendsIds(id);
        Set<Long> friends2 = getFriendsIds(friendId);
        return friends1.add(friendId) && friends2.add(id);
    }

    public boolean deleteFriend(@Positive long id, @Positive long friendId) {
        if (id == friendId) {
            throw new ValidationException("Can't delete yourself from friends");
        }
        Set<Long> friends1 = getFriendsIds(id);
        Set<Long> friends2 = getFriendsIds(friendId);
        return friends1.remove(friendId) && friends2.remove(id);
    }

    public List<User> getFriendsByUserId(@Positive long id) {
        Set<Long> friendsIds = getFriendsIds(id);
        return getAllUsers().stream()
                .filter(el -> friendsIds.contains(el.getId()))
                .toList();
    }

    public List<User> getCommonFriends(@Positive long id, @Positive long otherId) {
        Set<Long> friends1 = getFriendsIds(id);
        Set<Long> friends2 = getFriendsIds(otherId);
        return userStorage.getAllUsers().stream()
                .filter(el -> friends1.contains(el.getId()) && friends2.contains(el.getId()))
                .toList();
    }

    private Set<Long> getFriendsIds(long id) {
        Set<Long> friendsIds = userStorage.getFriendsIdsByUserId(id);
        if (friendsIds == null) {
            throw new NotFoundException("There is no user with id=" + id);
        }
        return friendsIds;
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
