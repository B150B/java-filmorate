package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.filmGenres.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmLike.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpaRating.MpaRatingStorage;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;
    private final MpaRatingStorage mpaRatingStorage;
    private final FilmLikeDbStorage filmLikeDbStorage;
    private final GenreDbStorage genreDbStorage;


    @Override
    public List<Film> getAllFilms() {
        log.info("Запрошен список всех фильмов");
        List<Film> films = jdbcTemplate.query(
                "SELECT * FROM films",
                new FilmMapper()
        );

        films.stream().forEach(film -> {
            MpaRating mpaRating = mpaRatingStorage.getMpaRating(film.getMpaRating().getId());
            film.setMpaRating(mpaRating);

            Set<Genre> genres = filmGenreStorage.getGenresByFilmId(film.getId());
            film.setGenres(genres);

            Set<Long> likedUserIds = filmLikeDbStorage.getLikesById(film.getId());
            film.setLikedUserIds(likedUserIds);
        });

        return films;
    }

    @Override
    public Film getFilm(Long id) {
        log.info("Запрошен фильм с id={}", id);
        Film film;

        try {
            film = jdbcTemplate.queryForObject(
                    "SELECT * FROM films WHERE id = ?",
                    new FilmMapper(),
                    id
            );
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }


        MpaRating mpaRating = mpaRatingStorage.getMpaRating(film.getMpaRating().getId());
        film.setMpaRating(mpaRating);

        Set<Genre> genres = filmGenreStorage.getGenresByFilmId(film.getId()).stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        film.setGenres(genres);

        Set<Long> likedUserIds = filmLikeDbStorage.getLikesById(film.getId());
        film.setLikedUserIds(likedUserIds);

        return film;
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Началось создание фильма");
        String sqlQuery = "INSERT INTO films (name,description,release_date,duration,mpa_id) " +
                "VALUES (?,?,?,?,?)";

        Long mpaId = null;

        if (film.getMpaRating() != null) {
            try {
                MpaRating mpaRating = mpaRatingStorage.getMpaRating(film.getMpaRating().getId());
                mpaId = mpaRating.getId();
            } catch (EmptyResultDataAccessException exception) {
                throw new NotFoundException("MPA с id=" + film.getMpaRating().getId() + " не существует");
            }
        }

        KeyHolder kh = new GeneratedKeyHolder();

        Long finalMpaId = mpaId;

        jdbcTemplate.update(conn -> {
                    var ps = conn.prepareStatement(sqlQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setObject(3, film.getReleaseDate());
                    ps.setInt(4, film.getDuration());
                    if (finalMpaId != null) {
                        ps.setLong(5, finalMpaId);
                    } else {
                        ps.setNull(5, Types.BIGINT);
                    }
                    return ps;
                }, kh
        );

        Long id = kh.getKey().longValue();

        film.setId(id);


        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                try {
                    genreDbStorage.getGenre(genre.getId());
                    filmGenreStorage.addGenreToFilm(id, genre.getId());
                } catch (EmptyResultDataAccessException exception) {
                    throw new NotFoundException("Жанр с id=" + genre.getId() + " не найден");
                }
            }
        }

        if (film.getLikedUserIds() != null) {
            for (Long userId : film.getLikedUserIds()) {
                filmLikeDbStorage.addLike(id, userId);
            }
        }

        log.info("Фильм с id={} успешно создан", id);
        return getFilm(id);

    }

    @Override
    public Film updateFilm(Film newFilm) {

        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id =?";

        Long mpaId = (newFilm.getMpaRating() != null) ? newFilm.getMpaRating().getId() : null;


        int updated = jdbcTemplate.update(sqlQuery,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                mpaId,
                newFilm.getId()
        );

        if (updated == 0) {
            throw new NotFoundException("Фильм с id= " + newFilm.getId() + " не найден");
        }

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", newFilm.getId());
        if (newFilm.getGenres() != null) {
            for (Genre genre : newFilm.getGenres()) {
                filmGenreStorage.addGenreToFilm(newFilm.getId(), genre.getId());
            }
        }

        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = ?", newFilm.getId());
        if (newFilm.getLikedUserIds() != null) {
            for (Long likedUserId : newFilm.getLikedUserIds()) {
                filmLikeDbStorage.addLike(newFilm.getId(), likedUserId);
            }
        }

        log.info("Фильм id={} успешно обновлен", newFilm.getId());
        return getFilm(newFilm.getId());
    }
}
