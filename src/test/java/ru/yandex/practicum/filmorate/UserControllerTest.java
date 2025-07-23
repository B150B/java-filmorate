package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User user1;
    private User user2;
    private User user3;


    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        user1 = new User("user1@gmail.com", "user1", "Мистер один", LocalDate.of(2000, 12, 12));
        user2 = new User("user2@gmail.com", "user2", "Мистер два", LocalDate.of(2001, 11, 11));
        user3 = null;
    }


    @Test
    void addingUser() {
        userController.createUser(user1);
        List<User> expexted = List.of(user1);
        List<User> result = userController.getAllUsers().stream().toList();
        assertArrayEquals(expexted.toArray(), result.toArray());
    }

    @Test
    void addingUsers() {
        userController.createUser(user1);
        userController.createUser(user2);
        List<User> expexted = List.of(user1, user2);
        List<User> result = userController.getAllUsers().stream().toList();
        assertArrayEquals(expexted.toArray(), result.toArray());
    }

    @Test
    void mustBeErrorWhenAddingUserWithoutAtInEmail() {
        User user3 = new User("user3gmail.com", "user3", "Мистер три", LocalDate.of(2001, 11, 11));
        assertThrows(ValidationException.class, () -> {
            userController.createUser(user3);
        });
    }

    @Test
    void mustBeErrorWhenAddingUserWithBlankEmail() {
        User user3 = new User("", "user3", "Мистер три", LocalDate.of(2001, 11, 11));
        assertThrows(ValidationException.class, () -> {
            userController.createUser(user3);
        });
    }

    @Test
    void mustBeErrorWhenAddingUserWithBlankLogin() {
        User user3 = new User("user3@gmail.com", "", "Мистер три", LocalDate.of(2001, 11, 11));
        assertThrows(ValidationException.class, () -> {
            userController.createUser(user3);
        });
    }

    @Test
    void mustBeErrorWhenAddingUserWithSpaceInLogin() {
        User user3 = new User("user3@gmail.com", "us er", "Мистер три", LocalDate.of(2001, 11, 11));
        assertThrows(ValidationException.class, () -> {
            userController.createUser(user3);
        });
    }

    @Test
    void ifNameisBlankThenMustUseLogin() {
        User user3 = new User("user3@gmail.com", "user3", "", LocalDate.of(2001, 11, 11));
        userController.createUser(user3);
        List<User> allUsers = userController.getAllUsers().stream().toList();
        User resultUser = allUsers.get(0);
        assertEquals(resultUser.getLogin(), resultUser.getName());
    }

    @Test
    void mustBeErrorWhenAddingUserWithDateOfRegInFuture() {
        User user3 = new User("user3@gmail.com", "user3", "Мистер три", LocalDate.of(2222, 11, 11));
        assertThrows(ValidationException.class, () -> {
            userController.createUser(user3);
        });
    }

    @Test
    void updateUser() {
        userController.createUser(user1);
        User user3 = new User("user3@gmail.com", "user3", "Мистер три", LocalDate.of(2001, 11, 11));
        userController.createUser(user3);
        user3.setId(1L);
        userController.updateUser(user3);
        List<User> userList = userController.getAllUsers().stream().toList();
        User result = userList.get(1);
        assertEquals(result, user3);
    }

    @Test
    void mustBeErrorWhenUpdateUserWithoutID() {
        User user3 = new User("user3@gmail.com", "user3", "Мистер три", LocalDate.of(2001, 11, 11));
        userController.createUser(user3);
        user3.setId(22L);
        assertThrows(NotFoundException.class, () -> {
            userController.updateUser(user3);
        });
    }


}
