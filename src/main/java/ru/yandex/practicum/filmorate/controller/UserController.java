package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.utils.BooleanAnswerBuilder.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        List<User> users = userService.getAllUsers();
        log.info("Successfully get users");
        return users;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);
            log.info("Successfully update user {}", updatedUser);
            return updatedUser;
        } catch (NotFoundException e) {
            log.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        User addedUser = userService.addUser(user);
        log.info("Successfully add user {}", addedUser);
        return addedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Map<String, String> addFriend(@PathVariable long id, @PathVariable long friendId) {
        boolean isSuccess = userService.addFriend(id, friendId);
        return isSuccess ? addFriendSuccessAnswer(id, friendId) : addFriendFailAnswer(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Map<String, String> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        boolean isSuccess = userService.deleteFriend(id, friendId);
        return isSuccess ? deleteFriendSuccessAnswer(id, friendId) : deleteFriendFailAnswer(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsByUserId(@PathVariable long id) {
        return userService.getFriendsByUserId(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
