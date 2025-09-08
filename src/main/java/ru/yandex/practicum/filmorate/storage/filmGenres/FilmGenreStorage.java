package ru.yandex.practicum.filmorate.storage.filmGenres;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenreStorage {
    Set<Genre> getGenresByFilmId(Long id);

    void addGenreToFilm(Long filmId, Long genreId);

    void removeGenreFromFilm(Long filmId, Long genreId);

}
