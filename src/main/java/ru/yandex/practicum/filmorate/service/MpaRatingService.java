package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpaRating.MpaRatingStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaRatingService {
    private final MpaRatingStorage mpaRatingStorage;

    public Set<MpaRating> getAllMpaRatings() {
        log.info("Запрошен список всех рейтингов");
        return mpaRatingStorage.getAllMpaRatings().stream()
                .sorted(Comparator.comparing(MpaRating::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public MpaRating getMpaRating(Long id) {
        log.info("Запрошен mpaRating с id = {}", id);
        return mpaRatingStorage.getMpaRating(id);
    }
}
