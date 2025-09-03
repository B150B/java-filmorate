package ru.yandex.practicum.filmorate.storage.Friendship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final Map<Long, Friendship> friendshipMap = new HashMap<>();
    private final UserStorage userStorage;

    public InMemoryFriendshipStorage(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private long getNextId() {
        long currentMaxId = friendshipMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public List<Friendship> getAllFriendship() {
        log.info("Запрошен список friendship");
        return friendshipMap.values().stream().toList();
    }

    @Override
    public Friendship deleteFriendship(Long id) {
        return friendshipMap.remove(id);
    }

    @Override
    public Friendship getFriendship(Long id) {
        if (!friendshipMap.containsKey(id)) {
            log.warn("Не найден нужный объект friendship в хранилище");
            throw new NotFoundException("Не найден объект friendship");
        }
        log.info("Запрошен объект friendship: " + friendshipMap.get(id));
        return friendshipMap.get(id);
    }

    @Override
    public Friendship getFriendshipByIds(Long userId, Long friendId) {
        return friendshipMap.values().stream()
                .filter(friendship ->
                        (friendship.getUserId().equals(userId)) && friendship.getFriendId().equals(friendId))
                .findFirst().get();
    }

    @Override
    public List<Friendship> getFriendshipById(Long userId) {
        return friendshipMap.values().stream()
                .filter(friendship ->
                        (friendship.getUserId().equals(userId)))
                .collect(Collectors.toUnmodifiableList());
    }

    public Friendship createFriendship(Friendship friendship) {
        if (userStorage.containsUser(friendship.getUserId()) || userStorage.containsUser(friendship.getFriendId())) {
            friendship.setId(getNextId());
            friendshipMap.put(friendship.getId(), friendship);
            log.info("Добавлен новый объект friendship: " + friendship.toString());
            return friendship;
        } else throw new NotFoundException("Не обнаружен пользователь для добавления в friendship");
    }

    @Override
    public Friendship updateFriendship(Friendship friendship) {
        if (friendship.getUserId() == null || friendship.getFriendId() == null) {
            log.warn("Валидация не пройдена - не указан ID в методе PUT");
            throw new ValidationException("Должен быть указан id");
        }
        if (!userStorage.containsUser(friendship.getUserId()) || !userStorage.containsUser(friendship.getFriendId())) {
            throw new NotFoundException("Не обнаружен пользователь для добавления в friendship");
        }

        Friendship oldFriendship = friendshipMap.get(friendship.getId());
        oldFriendship.setUserId(friendship.getUserId());
        oldFriendship.setFriendId(friendship.getFriendId());
        oldFriendship.setFriendshipConfirmed(friendship.isFriendshipConfirmed());

        log.info("Обновлена информация об объекте Friendship:" + oldFriendship.toString());
        return oldFriendship;
    }

    public boolean containsFriendship(Long userId, Long friendId) {
        return friendshipMap.values().stream()
                .anyMatch(friendship ->
                        (friendship.getUserId().equals(userId)) && friendship.getFriendId().equals(friendId));
    }


}
