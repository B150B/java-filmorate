package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    FilmStorage filmStorage;
    UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
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

    public String setLike(Long userid, Long filmId) {
        Film film = filmStorage.getFilm(filmId);
        film.getLikedUserIds().add(userid);
        String result = String.format("Пользователь %s поставил лайк фильму %s",
                userService.getUser(userid),
                filmStorage.getFilm(filmId));
        log.info(result);
        return result;
    }

    public String removeLike(Long userid, Long filmId) {
        Film film = filmStorage.getFilm(filmId);
        film.getLikedUserIds().remove(userid);
        String result = String.format("Пользователь %s удалил лайк фильму %s",
                userService.getUser(userid),
                filmStorage.getFilm(filmId));
        log.info(result);
        return result;
    }

    public List<Film> getListOfPopularFilms(Integer limit) {
        log.info("Запрошен список " + limit + " самых популярных фильмов");
        return filmStorage.getAllFilms().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikedUserIds().size(), film1.getLikedUserIds().size()))
                .limit(limit)
                .collect(Collectors.toUnmodifiableList());
    }

}
