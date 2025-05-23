package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.utils.OperationType;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final EventService eventService;
    private final ValidationService validationService;

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User updateUser(User user) {
        validationService.validateUserById(user.getId());
        validateName(user);
        return repository.update(user);
    }

    public User addUser(User user) {
        validateName(user);
        return repository.save(user);
    }

    public boolean addFriend(long id, long friendId) {
        if (id == friendId) {
            throw new ValidationException("Can't add yourself as a friend");
        }
        validationService.validateUserById(id, friendId);
        eventService.createFriendEvent(id, friendId, OperationType.ADD);
        return repository.addFriendshipRow(id, friendId);
    }

    public boolean deleteFriend(@Positive long id, @Positive long friendId) {
        if (id == friendId) {
            throw new ValidationException("Can't delete yourself from friends");
        }
        validationService.validateUserById(id, friendId);
        eventService.createFriendEvent(id, friendId, OperationType.REMOVE);
        return repository.deleteFriendshipRow(id, friendId);
    }

    public List<User> getFriendsByUserId(@Positive long id) {
        validationService.validateUserById(id);
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

    public boolean deleteUserById(long userId) {
        return repository.deleteById(userId);
    }

    public User getUserById(long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("There is no user with id=" + userId));
    }
}
