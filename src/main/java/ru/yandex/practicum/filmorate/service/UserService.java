package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User updateUser(@Valid User user) {
        validateUser(user.getId());
        validateName(user);
        return repository.update(user);
    }

    public User addUser(@Valid User user) {
        validateName(user);
        return repository.save(user);
    }

    public boolean addFriend(@Positive long id, @Positive long friendId) {
        if (id == friendId) {
            throw new ValidationException("Can't add yourself as a friend");
        }
        validateUser(id, friendId);

        return repository.addFriendshipRow(id, friendId);
    }

    public boolean deleteFriend(@Positive long id, @Positive long friendId) {
        if (id == friendId) {
            throw new ValidationException("Can't delete yourself from friends");
        }
        validateUser(id, friendId);
        return repository.deleteFriendshipRow(id, friendId);
    }

    public List<User> getFriendsByUserId(@Positive long id) {
        validateUser(id);
        return repository.getFriendsByUserId(id);
    }

    public List<User> getCommonFriends(@Positive long id, @Positive long otherId) {
        if (id == otherId) {
            throw new ValidationException("Put different user ids");
        }
        validateUser(id, otherId);
        List<User> friendsByUserId1 = getFriendsByUserId(id);
        List<User> friendsByUserId2 = getFriendsByUserId(otherId);
        return friendsByUserId1.stream().filter(friendsByUserId2::contains).toList();
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateUser(long... ids) {
        for (long id : ids) {
            if (!repository.existById(id)) throw new NotFoundException("There is no user with id=" + id);
        }
    }
}
