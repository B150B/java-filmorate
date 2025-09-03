package ru.yandex.practicum.filmorate.storage.mpaRating;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingStorage {
    List<MpaRating> getAllMpaRatings();

    MpaRating getMpaRating(Long id);
}
