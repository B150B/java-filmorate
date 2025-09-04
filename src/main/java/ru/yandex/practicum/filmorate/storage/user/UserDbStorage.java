package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.mapper.UserMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;

import java.sql.PreparedStatement;
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

        List<Friendship> friendships = friendshipStorage.getFriendshipById(user.getId());
        if (!friendships.isEmpty()) {
            Set<Long> friendsId = new HashSet<>();
            for (Friendship friendship : friendships) {
                if (containsUser(friendship.getFriendId())) {
                    friendsId.add(friendship.getFriendId());
                }
            }
            user.setFriendsIds(friendsId);
        }
        log.info("Пользователь с id = {} успешно возвращен", id);
        return user;
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO users (email,login,name,birthday) VALUES (?,?,?,?)";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
                    var ps = conn.prepareStatement(sqlQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setString(1, user.getEmail());
                    ps.setString(2, user.getLogin());
                    ps.setString(3, user.getName());
                    ps.setObject(4, user.getBirthday());
                    return ps;
                }, kh
        );

        Long id = kh.getKey().longValue();

        user.setId(id);

        if (user.getFriendsIds() != null) {
            for (Long friendId : user.getFriendsIds()) {
                friendshipStorage.createFriendship(new Friendship(user.getId(), friendId));
            }
        }

        log.info("Пользователь с id={} успешно создан", id);
        return getUser(id);
    }

    @Override
    public User updateUser(User newUser) {

        String sqlQuery = "UPDATE users SET email = ?, login = ?, name =?, birthday = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sqlQuery,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );

        if (updated == 0) {
            throw new NotFoundException("Пользователь с id=" + newUser.getId() + " не найден");
        }


        jdbcTemplate.update("DELETE FROM friendships WHERE user_id = ?", newUser.getId());
        if (newUser.getFriendsIds() != null) {
            for (Long friendId : newUser.getFriendsIds()) {
                friendshipStorage.createFriendship(new Friendship(newUser.getId(), friendId));
            }
        }
        log.info("Пользователь с id = {} успешно обновлен", newUser.getId());
        return getUser(newUser.getId());
    }

    @Override
    public boolean containsUser(Long id) {
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS( SELECT 1 FROM users WHERE id = ?)",
                Boolean.class,
                id
        );

        return exists;
    }
}
