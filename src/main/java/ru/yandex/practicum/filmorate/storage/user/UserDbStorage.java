package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.mapper.UserMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Friendship.FriendshipStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipStorage friendshipStorage;

    public UserDbStorage(@Qualifier("friendshipDbStorage") FriendshipStorage friendshipStorage, JdbcTemplate jdbcTemplate) {
        this.friendshipStorage = friendshipStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Запрошен список всех пользователей");
        return jdbcTemplate.query(
                "SELECT * FROM users",
                new UserMapper()
        );
    }

    @Override
    public User getUser(Long id) {
        log.info("Запрошен пользователь " + id);
        User user = jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE id = ?",
                new UserMapper(),
                id
        );

        if (!friendshipStorage.getFriendshipById(user.getId()).isEmpty()) {
            List<Friendship> friendships = friendshipStorage.getFriendshipById(user.getId());
            Set<Long> friendsId = new HashSet<>();
            for (Friendship friendship : friendships) {
                if (containsUser(friendship.getFriendId())) {
                    friendsId.add(friendship.getFriendId());
                }
            }
            user.setFriendsIds(friendsId);
        }
        log.info("Пользователь с " + id + " успешно возвращен");
        return user;
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO users (email,login,name,birthday) VALUES (?,?,?,?)";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );

        Long id = jdbcTemplate.queryForObject("SELECT MAX (id) FROM users", Long.class);

        user.setId(id);

        if (user.getFriendsIds() != null) {
            for (Long friendId : user.getFriendsIds()) {
                friendshipStorage.createFriendship(new Friendship(user.getId(), friendId));
            }
        }

        log.info("Пользователь с " + id + " успешно создан");
        return getUser(id);
    }

    @Override
    public User updateUser(User newUser) {

        try {
            User oldUser = jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE ID = ?",
                    new UserMapper(),
                    newUser.getId()
            );
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("Пользователь с id=" + newUser.getId() + " не найден");
        }

        String sqlQuery = "UPDATE users SET email = ?, login = ?, name =?, birthday = ?";
        int updated = jdbcTemplate.update(sqlQuery,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday()
        );


        jdbcTemplate.update("DELETE FROM friendships WHERE user_id = ?", newUser.getId());
        if (newUser.getFriendsIds() != null) {
            for (Long friendId : newUser.getFriendsIds()) {
                friendshipStorage.createFriendship(new Friendship(newUser.getId(), friendId));
            }
        }
        log.info("Пользователь с " + newUser.getId() + " успешно обновлен");
        return getUser(newUser.getId());
    }

    @Override
    public boolean containsUser(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = ?",
                Integer.class,
                id
        );

        return count != 0;
    }
}
