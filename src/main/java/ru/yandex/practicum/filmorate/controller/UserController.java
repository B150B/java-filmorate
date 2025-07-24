package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> userMap = new HashMap<>();

    private long getNextId() {
        long currentMaxId = userMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрошен список пользователей");
        return userMap.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if ((user.getEmail().isBlank()) || !(user.getEmail().contains("@"))) {
            log.warn("Ошибка валидации - Электронная почта не может быть пустой и должна содержать символ \"@\"");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ \"@\" ");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации - Логин не может быть пустым или содержать пробелы");
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.warn("Ошибка валидации - имя пустое - вместо него используется логин");
            System.out.println("Имя пустое - вместо него используется логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации - Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        user.setId(getNextId());
        userMap.put(user.getId(), user);
        log.info("Создан новый пользователь: " + user.toString());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Валидация не пройдена - не указан ID в методе PUT");
            throw new ValidationException("Должен быть указан id");
        }
        User oldUser;
        if (!userMap.containsKey(newUser.getId())) {
            log.warn("Не найден пользователь с указанным id при методе PUT");
            throw new NotFoundException("Не найден пользователь с указанным id");
        } else {
            oldUser = userMap.get(newUser.getId());
        }

        if ((newUser.getEmail().isBlank()) || !(newUser.getEmail().contains("@"))) {
            log.warn("Ошибка валидации - Электронная почта не может быть пустой и должна содержать символ \"@\"");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ \"@\" ");
        }
        if (newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            log.warn("Ошибка валидации - Логин не может быть пустым или содержать пробелы");
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации - Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (newUser.getName() != null || !newUser.getName().isBlank()) {
            log.info("Обновлено отображаемое имя пользователя:" + newUser.getName());
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            log.info("Обновлен email пользователя:" + newUser.getEmail());
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            log.info("Обновлен логин пользователя:" + newUser.getLogin());
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getBirthday() != null) {
            log.info("Обновлена дата рождения пользователя:" + newUser.getBirthday());
            oldUser.setBirthday(newUser.getBirthday());
        }

        log.info("Обновлен пользователь: " + oldUser.toString());
        return oldUser;

    }

}
