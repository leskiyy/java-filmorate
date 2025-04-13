package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final EventService eventService;
    private final ValidationService validationService;
    private static final String METHOD_ADD = "ADD";
    private static final String METHOD_REMOVE = "REMOVE";

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User updateUser(@Valid User user) {
        validationService.validateUserById(user.getId());
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
        validationService.validateUserById(id, friendId);
        eventService.createFriendEvent(id, friendId, METHOD_ADD);
        return repository.addFriendshipRow(id, friendId);
    }

    public boolean deleteFriend(@Positive long id, @Positive long friendId) {
        if (id == friendId) {
            throw new ValidationException("Can't delete yourself from friends");
        }
        validationService.validateUserById(id, friendId);
        eventService.createFriendEvent(id, friendId, METHOD_REMOVE);
        return repository.deleteFriendshipRow(id, friendId);
    }

    public List<User> getFriendsByUserId(@Positive long id) {
        validationService.validateForFriends(id);
        return repository.getFriendsByUserId(id);
    }

    public List<User> getCommonFriends(@Positive long id, @Positive long otherId) {
        if (id == otherId) {
            throw new ValidationException("Put different user ids");
        }
        validationService.validateUserById(id, otherId);
        List<User> friendsByUserId1 = getFriendsByUserId(id);
        List<User> friendsByUserId2 = getFriendsByUserId(otherId);
        return friendsByUserId1.stream().filter(friendsByUserId2::contains).toList();
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void deleteUserById(long id) {
        repository.deleteUserById(id);
    }

    public User getUserById(long id) {
        validationService.validateUserById(id);
        return repository.getUserById(id);
    }
}
