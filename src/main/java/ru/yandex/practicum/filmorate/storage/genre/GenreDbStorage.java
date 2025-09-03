package ru.yandex.practicum.filmorate.storage.genre;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Genre> getAllGenres() {
        log.info("Запрошен список всех жанров");
        String sqlQuery = "SELECT id, name FROM genres";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, new GenreMapper()));
    }

    @Override
    public Genre getGenre(Long id) {
        log.info("Запрошен жанр с id=" + id);
        String sqlQuery = "SELECT id, name FROM genres WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new GenreMapper(), id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("Жанр с id=" + id + " не найден");
        }
    }
}
