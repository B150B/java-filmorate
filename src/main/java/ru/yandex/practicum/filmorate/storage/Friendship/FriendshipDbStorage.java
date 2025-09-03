package ru.yandex.practicum.filmorate.storage.Friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.mapper.FriendshipMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Friendship> getAllFriendship() {
        log.info("Запрошен cписок всех Friendship");
        return jdbcTemplate.query(
                "SELECT * FROM friendships",
                new FriendshipMapper()
        );
    }

    @Override
    public Friendship getFriendship(Long id) {
        log.info("Запрошен Friendship с id=" + id);
        return jdbcTemplate.queryForObject(
                "SELECT * FROM friendships WHERE id = ?",
                new FriendshipMapper(),
                id
        );

    }

    @Override
    public Friendship deleteFriendship(Long id) {

        Friendship friendship = getFriendship(id);

        jdbcTemplate.update(
                "DELETE FROM friendships WHERE id = ?",
                id
        );
        log.info("Удален Friendship с id=" + id);
        return friendship;
    }

    @Override
    public List<Friendship> getFriendshipById(Long userId) {
        log.info("Запрошены объекты Friendship пользователя с id=" + userId);
        return jdbcTemplate.query(
                "SELECT * FROM friendships WHERE user_id = ?",
                new FriendshipMapper(),
                userId
        );
    }

    @Override
    public Friendship getFriendshipByIds(Long userId, Long friendId) {
        log.info("Запрошен объект Friendship пользователя " + userId + " и пользователя " + friendId);
        return jdbcTemplate.queryForObject(
                "SELECT * FROM friendships WHERE user_id = ? AND friend_id = ?",
                new FriendshipMapper(),
                userId,
                friendId
        );
    }

    @Override
    public Friendship createFriendship(Friendship friendship) {
        String sqlQuery = "INSERT INTO friendships (user_id,friend_id,friendship_confirmed) " +
                "VALUES (?,?,?)";
        jdbcTemplate.update(sqlQuery,
                friendship.getUserId(),
                friendship.getFriendId(),
                friendship.isFriendshipConfirmed()
        );

        Long id = jdbcTemplate.queryForObject("SELECT MAX (id) FROM friendships", Long.class);

        friendship.setId(id);

        log.info("Создан объект Friendship с id=" + id);
        return getFriendship(id);

    }

    @Override
    public Friendship updateFriendship(Friendship friendship) {
        String sqlQuery = "UPDATE friendships set user_id = ?, friend_id = ?,friendship_confirmed = ? WHERE ID = ?";
        int updated = jdbcTemplate.update(sqlQuery,
                friendship.getUserId(),
                friendship.getFriendId(),
                friendship.isFriendshipConfirmed(),
                friendship.getId()
        );

        if (updated == 0) {
            throw new NotFoundException("Объект friendship с id= " + friendship.getId() + " не найден");
        }


        log.info("Обновлен объект Friendship с id=" + friendship.getId());
        return getFriendship(friendship.getId());
    }

    @Override
    public boolean containsFriendship(Long userId, Long friendId) {
        log.info("Проверка объекта Friendship пользователя " + userId + " и пользователя " + friendId);
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?",
                Integer.class,
                userId,
                friendId
        );
        return count != 0;
    }
}
