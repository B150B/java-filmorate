package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriendList(@PathVariable Long userId) {
        return userService.getFriendList(userId);
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable Long userId,
                                       @PathVariable Long friendId) {
        return userService.getCommonFriends(userId, friendId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        return userService.updateUser(newUser);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public String addFriend(@PathVariable Long userId,
                            @PathVariable Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public String removFriend(@PathVariable Long userId,
                              @PathVariable Long friendId) {
        return userService.removeFriend(userId, friendId);
    }


}
