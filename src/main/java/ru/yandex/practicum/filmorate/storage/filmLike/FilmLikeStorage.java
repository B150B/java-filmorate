package ru.yandex.practicum.filmorate.storage.filmLike;

import java.util.Set;

public interface FilmLikeStorage {
    Set<Long> getLikesById(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);
}
