package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> userMap = new HashMap<>();
    private final Map<Long, Friendship> friendshipMap = new HashMap<>();

    private long getNextId() {
        long currentMaxId = userMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public List<User> getAllUsers() {
        log.info("Запрошен список пользователей");
        return userMap.values().stream().toList();
    }

    public User getUser(Long id) {
        if (!userMap.containsKey(id)) {
            log.warn("Не найден нужный пользователь в хранилище");
            throw new NotFoundException("Не найден указанный пользователь");
        }
        log.info("Запрошен пользователь " + userMap.get(id));
        return userMap.get(id);
    }

    public User createUser(User user) {

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации - Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        user.setId(getNextId());
        userMap.put(user.getId(), user);
        log.info("Создан новый пользователь: " + user.toString());
        return user;
    }


    public User updateUser(User newUser) {
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

        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации - Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        log.info("Обновлено отображаемое имя пользователя:" + newUser.getName());
        oldUser.setName(newUser.getName());
        log.info("Обновлен email пользователя:" + newUser.getEmail());
        oldUser.setEmail(newUser.getEmail());
        log.info("Обновлен логин пользователя:" + newUser.getLogin());
        oldUser.setLogin(newUser.getLogin());
        log.info("Обновлена дата рождения пользователя:" + newUser.getBirthday());
        oldUser.setBirthday(newUser.getBirthday());

        log.info("Обновлен пользователь: " + oldUser.toString());
        return oldUser;

    }

    public boolean containsUser(Long id) {
        return userMap.containsKey(id);
    }


}
