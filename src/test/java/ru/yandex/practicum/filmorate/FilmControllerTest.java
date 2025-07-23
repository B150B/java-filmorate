package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private FilmController filmController;
    private Film film1;
    private Film film2;
    private Film film3;


    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film1 = new Film("Фильм1", "Описание фильма1", LocalDate.of(2001, 11, 11), Duration.ofMinutes(50));
        film2 = new Film("Фильм2", "Описание фильма2", LocalDate.of(2003, 10, 13), Duration.ofMinutes(80));
        film3 = null;
    }


    @Test
    void addingFilm() {
        filmController.createFilm(film1);
        List<Film> expexted = List.of(film1);
        List<Film> result = filmController.getAllFilms().stream().toList();
        assertArrayEquals(expexted.toArray(), result.toArray());
    }

    @Test
    void addingFilms() {
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        List<Film> expexted = List.of(film1, film2);
        List<Film> result = filmController.getAllFilms().stream().toList();
        assertArrayEquals(expexted.toArray(), result.toArray());
    }

    @Test
    void mustBeErrorWhenAddingFilmWithoutName() {
        film3 = new Film("", "Описание фильма3", LocalDate.of(2000, 1, 22), Duration.ofMinutes(88));
        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film3);
        });
    }

    @Test
    void mustBeErrorWhenAddingFilmBefore1895() {
        film3 = new Film("Фильм3", "Описание фильма3", LocalDate.of(1890, 1, 22), Duration.ofMinutes(88));
        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film3);
        });
    }

    @Test
    void mustBeErrorWhenAddingFilmWithNegativeDuration() {
        film3 = new Film("Фильм3", "Описание фильма3", LocalDate.of(2000, 1, 22), Duration.ofMinutes(-88));
        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film3);
        });
    }

    @Test
    void mustBeErrorWhenAddingFilmWithMoreThan200SymbolsDescription() {
        film3 = new Film("Фильм3", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                , LocalDate.of(2000, 1, 22), Duration.ofMinutes(88));
        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film3);
        });
    }


    @Test
    void updateFilm() {
        filmController.createFilm(film1);
        film3 = new Film("Фильм3", "Описание фильма3", LocalDate.of(2000, 1, 22), Duration.ofMinutes(88));
        filmController.createFilm(film3);
        film3.setId(1L);
        filmController.updateFilm(film3);
        List<Film> filmList = filmController.getAllFilms().stream().toList();
        Film result = filmList.get(1);
        assertEquals(result, film3);
    }

    @Test
    void MustBeErrorWhenUpdateFilmWithoutID() {
        film3 = new Film("Фильм3", "Описание фильма3", LocalDate.of(2000, 1, 22), Duration.ofMinutes(88));
        filmController.createFilm(film3);
        film3.setId(22L);
        assertThrows(NotFoundException.class, () -> {
            filmController.updateFilm(film3);
        });
    }


}
