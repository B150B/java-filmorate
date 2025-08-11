package ru.yandex.practicum.filmorate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    private FilmController filmController;
    private Film film1;
    private Film film2;
    private Film film3;


    @BeforeEach
    public void beforeEach() {
        InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService(inMemoryUserStorage);
        FilmService filmService = new FilmService(inMemoryFilmStorage, userService);

        filmController = new FilmController(filmService, inMemoryUserStorage);
        film1 = new Film("Фильм1", "Описание фильма1", LocalDate.of(2001, 11, 11), 50);
        film2 = new Film("Фильм2", "Описание фильма2", LocalDate.of(2003, 10, 13), 80);
        film3 = null;
    }


    @Test
    void addingFilm() {
        filmController.createFilm(film1);
        List<Film> expexted = List.of(film1);
        List<Film> result = filmController.getAllFilms();
        assertArrayEquals(expexted.toArray(), result.toArray());
    }

    @Test
    void addingFilms() {
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        List<Film> expexted = List.of(film1, film2);
        List<Film> result = filmController.getAllFilms();
        assertArrayEquals(expexted.toArray(), result.toArray());
    }

    @Test
    void mustBeErrorWhenAddingFilmWithoutName() throws Exception {
        film3 = new Film("", "Описание фильма3", LocalDate.of(2000, 1, 22), 88);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(film3)))
                .andExpect(status().isBadRequest());
        ;
    }

    @Test
    void mustBeErrorWhenAddingFilmBefore1895() throws Exception {
        film3 = new Film("Фильм3", "Описание фильма3", LocalDate.of(1890, 1, 22), 88);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(film3)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustBeErrorWhenAddingFilmWithNegativeDuration() throws Exception {
        film3 = new Film("Фильм3", "Описание фильма3", LocalDate.of(2000, 1, 22), -88);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(film3)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustBeErrorWhenAddingFilmWithMoreThan200SymbolsDescription() throws Exception {
        String description = "a".repeat(201);
        film3 = new Film("Фильм3", description,
                LocalDate.of(2000, 1, 22), 88);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(film3)))
                .andExpect(status().isBadRequest());
        ;
    }


    @Test
    void updateFilm() {
        filmController.createFilm(film1);
        film3 = new Film("Фильм3", "Описание фильма3", LocalDate.of(2000, 1, 22), 88);
        filmController.createFilm(film3);
        film3.setId(1L);
        filmController.updateFilm(film3);
        List<Film> filmList = filmController.getAllFilms();
        Film result = filmList.get(1);
        assertEquals(result, film3);
    }

    @Test
    void mustBeErrorWhenUpdateFilmWithoutID() {
        film3 = new Film("Фильм3", "Описание фильма3", LocalDate.of(2000, 1, 22), 88);
        filmController.createFilm(film3);
        film3.setId(22L);
        assertThrows(NotFoundException.class, () -> {
            filmController.updateFilm(film3);
        });
    }


}
