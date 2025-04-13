package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.EventDTO;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
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
    private final EventService eventService;

    @GetMapping
    public List<User> getUsers() {
        List<User> users = userService.getAllUsers();
        log.info("Successfully get users");
        return users;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        log.info("Successfully update user {}", updatedUser);
        return updatedUser;
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

    @GetMapping("/{id}/feed")
    public List<EventDTO> getFeedByUserId(@PathVariable long id) {
        return eventService.getUserFeed(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public void getUserById(@PathVariable long id) {
        userService.getUserById(id);
    }


}
