package ru.yandex.practicum.filmorate.storage.mpaRating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;


import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpaRatingDbStorage implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaRating> getAllMpaRatings() {
        log.info("Запрошен список всех MpaRating");
        return jdbcTemplate.query(
                "SELECT id,name FROM mpa_ratings",
                new MpaRatingMapper()
        );
    }

    @Override
    public MpaRating getMpaRating(Long id) {
        try {
            log.info("Запрошен MpaRating с id=" + id);
            return jdbcTemplate.queryForObject(
                    "SELECT id,name FROM mpa_ratings WHERE id = ?",
                    new MpaRatingMapper(),
                    id
            );
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("MpaRating с id=" + id + " не найден");
        }
    }
}
