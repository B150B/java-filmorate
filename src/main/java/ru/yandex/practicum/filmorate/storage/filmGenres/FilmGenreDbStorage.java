package ru.yandex.practicum.filmorate.storage.filmGenres;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
@RequiredArgsConstructor
@Slf4j
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;


    @Override
    public Set<Genre> getGenresByFilmId(Long id) {
        log.info("Запрошен список жанров у фильма id=" + id);
        List<Long> genreIds = jdbcTemplate.queryForList(
                "SELECT genre_id FROM film_genres WHERE film_id = ?",
                Long.class,
                id
        );

        Set<Genre> resultGenres = new HashSet<>();
        genreIds.stream().forEach(genreId -> resultGenres.add(genreStorage.getGenre(genreId)));

        return resultGenres;
    }

    @Override
    public void addGenreToFilm(Long filmId, Long genreId) {
        jdbcTemplate.update(
                "INSERT INTO film_genres (film_id, genre_id) VALUES (?,?)",
                filmId,
                genreId
        );
        log.info("Фильму id= " + filmId + " добавлен жанр id=" + genreId);
    }

    @Override
    public void removeGenreFromFilm(Long filmId, Long genreId) {
        jdbcTemplate.update(
                "DELETE FROM film_genres WHERE film_id = ? AND genre_id = ? ",
                filmId,
                genreId
        );
        log.info("У фильма id= " + filmId + " убран жанр id=" + genreId);
    }
}
