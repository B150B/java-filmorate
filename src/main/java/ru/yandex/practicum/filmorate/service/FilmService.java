package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmLike.FilmLikeStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FilmLikeStorage filmLikeStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService,
                       FilmLikeStorage filmLikeStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.filmLikeStorage = filmLikeStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilm(id);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public void setLike(Long userid, Long filmId) {
        User user = userService.getUser(userid); // при таком получении пользователя проверяется его наличие в базе
        Film film = filmStorage.getFilm(filmId);
        film.getLikedUserIds().add(userid);
        filmLikeStorage.addLike(filmId, userid);
        log.info("Пользователь {} поставил лайк фильму {}",
                user,
                film);
    }

    public void removeLike(Long userid, Long filmId) {
        User user = userService.getUser(userid); // при таком получении пользователя проверяется его наличие в базе
        Film film = filmStorage.getFilm(filmId);
        film.getLikedUserIds().remove(userid);
        filmLikeStorage.removeLike(filmId, userid);
        log.info("Пользователь {} удалил лайк фильму {}",
                user,
                film);
    }

    public List<Film> getListOfPopularFilms(Integer limit) {
        log.info("Запрошен список {} самых популярных фильмов", limit);
        return filmStorage.getAllFilms().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikedUserIds().size(), film1.getLikedUserIds().size()))
                .limit(limit)
                .collect(Collectors.toUnmodifiableList());
    }

}
