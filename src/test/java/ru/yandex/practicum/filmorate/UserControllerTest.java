package ru.yandex.practicum.filmorate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        List<User> result = userController.getAllUsers();
        assertArrayEquals(expexted.toArray(), result.toArray());
    }

    @Test
    void addingUsers() {
        userController.createUser(user1);
        userController.createUser(user2);
        List<User> expexted = List.of(user1, user2);
        List<User> result = userController.getAllUsers();
        assertArrayEquals(expexted.toArray(), result.toArray());
    }

    @Test
    void mustBeErrorWhenAddingUserWithoutAtInEmail() throws Exception {
        User user3 = new User("user3gmail.com", "user3", "Мистер три", LocalDate.of(2001, 11, 11));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user3)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustBeErrorWhenAddingUserWithBlankEmail() throws Exception {
        User user3 = new User("", "user3", "Мистер три", LocalDate.of(2001, 11, 11));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user3)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void mustBeErrorWhenAddingUserWithBlankLogin() throws Exception {
        User user3 = new User("user3@gmail.com", "", "Мистер три", LocalDate.of(2001, 11, 11));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user3)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustBeErrorWhenAddingUserWithSpaceInLogin() throws Exception {
        User user3 = new User("user3@gmail.com", "us er", "Мистер три", LocalDate.of(2001, 11, 11));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user3)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ifNameisBlankThenMustUseLogin() {
        User user3 = new User("user3@gmail.com", "user3", "", LocalDate.of(2001, 11, 11));
        userController.createUser(user3);
        List<User> allUsers = userController.getAllUsers();
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
        List<User> userList = userController.getAllUsers();
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
