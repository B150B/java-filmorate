package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmMap = new HashMap<>();

    private long getNextId() {
        long currentMaxId = filmMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public List<Film> getAllFilms() {
        log.info("Запрошен список фильмов");
        return filmMap.values().stream().toList();
    }

    public Film getFilm(Long id) {
        if (!filmMap.containsKey(id)) {
            log.warn("Не найден нужный фильм в хранилище");
            throw new NotFoundException("Не найден указанный фильм");
        }
        log.info("Запрошен фильм: " + filmMap.get(id));
        return filmMap.get(id);
    }

    public Film createFilm(Film film) {
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        log.info("Добавлен новый фильм: " + film.toString());
        return film;
    }

    public Film updateFilm(Film newFilm) {
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

        log.info("Обновлено название фильма:" + newFilm.getName());
        oldFilm.setName(newFilm.getName());
        log.info("Обновлено описание фильма:" + newFilm.getDescription());
        oldFilm.setDescription(newFilm.getDescription());
        log.info("Обновлено дата релиза фильма:" + newFilm.getReleaseDate());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        log.info("Обновлена продолжительность фильма:" + newFilm.getDuration());
        oldFilm.setDuration(newFilm.getDuration());

        log.info("Обновлена информация о фильме:" + oldFilm.toString());
        return oldFilm;
    }

}
