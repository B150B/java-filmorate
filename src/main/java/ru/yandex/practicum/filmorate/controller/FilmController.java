package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> filmMap = new HashMap<>();

    private long getNextId() {
        long currentMaxId = filmMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Запрошен список фильмов");
        return filmMap.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация не пройдена - пустое название");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Валидация не пройдена - превышена максимальная длина описания");
            throw new ValidationException("Максимальная длина описания (200 символов) превышена");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Валидация не пройдена - дата релиза раньше 28.12.1895");
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDuration().isNegative()) {
            log.warn("Валидация не пройдена - отрицательная продолжительность фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        log.info("Добавлен новый фильм: " + film.toString());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Валидация не пройдена - не указан ID в методе PUT");
            throw new ValidationException("Должен быть указан id");
        }
        Film oldFilm;
        if (filmMap.containsKey(newFilm.getId())) {
            oldFilm = filmMap.get(newFilm.getId());
        } else {
            log.warn("Не найден фильм с указанным id при методе PUT");
            throw new NotFoundException("Не найден фильм с указанным id");
        }
        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            log.warn("Валидация не пройдена - пустое название");
            throw new ValidationException("Название не может быть пустым");
        }
        if ( newFilm.getDescription().length() > 200) {
            log.warn("Валидация не пройдена - превышена максимальная длина описания");
            throw new ValidationException("Максимальная длина описания (200 символов) превышена");
        }
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Валидация не пройдена - дата релиза раньше 28.12.1895");
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (newFilm.getDuration().isNegative()) {
            log.warn("Валидация не пройдена - отрицательная продолжительность фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if (newFilm.getName() != null) {
            log.info("Обновлено название фильма:" + newFilm.getName());
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            log.info("Обновлено описание фильма:" + newFilm.getDescription());
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            log.info("Обновлено дата релиза фильма:" + newFilm.getReleaseDate());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null) {
            log.info("Обновлена продолжительность фильма:" + newFilm.getDuration());
            oldFilm.setDuration(newFilm.getDuration());
        }
        log.info("Обновлена информация о фильме:" + oldFilm.toString());
        return oldFilm;
    }


}
