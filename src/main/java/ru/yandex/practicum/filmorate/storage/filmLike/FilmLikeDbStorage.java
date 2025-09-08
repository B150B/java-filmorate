package ru.yandex.practicum.filmorate.storage.filmLike;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmLikeDbStorage implements FilmLikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Long> getLikesById(Long id) {
        log.info("Запрошен список лайков фильма с id = " + id);
        return new HashSet<>(jdbcTemplate.queryForList(
                "SELECT user_id FROM film_likes WHERE film_id = ?",
                Long.class,
                id
        ));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbcTemplate.update(
                "INSERT INTO film_likes (film_id,user_id) VALUES (?,?)",
                filmId,
                userId
        );
        log.info("Пользователь  id={} поставил лайк фильму id={}", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbcTemplate.update(
                "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?",
                filmId,
                userId
        );
        log.info("Пользователь id = {} удалил лайк у фильма id = {}", userId, filmId);
    }
}
