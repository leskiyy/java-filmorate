package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;

    public UserController() {
        userStorage = new UserStorage();
    }

    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public List<User> getUsers() {
        List<User> users = userStorage.getAllUsers();
        log.info("Successfully get users");
        return users;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        try {
            User updatedUser = userStorage.updateUser(user);
            log.info("Successfully update user {}", updatedUser);
            return updatedUser;
        } catch (NotFoundException e) {
            log.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        User addedUser = userStorage.addUser(user);
        log.info("Successfully add user {}", addedUser);
        return addedUser;
    }
}
