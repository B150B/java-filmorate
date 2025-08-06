package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {


    private final UserStorage userStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService, UserStorage userStorage) {
        this.userStorage = userStorage;
        this.filmService = filmService;
    }


    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable Long filmId) {
        return filmService.getFilm(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getListOfPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getListOfPopularFilms(count);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public String setLike(@PathVariable Long filmId,
                          @PathVariable Long userId) {
        return filmService.setLike(userId, filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public String removeLike(@PathVariable Long filmId,
                             @PathVariable Long userId) {
        return filmService.removeLike(userId, filmId);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }


}
