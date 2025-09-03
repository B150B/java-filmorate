package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaRatingService mpaRatingService;

    @GetMapping
    public Set<MpaRating> getAllGenres() {
        return mpaRatingService.getAllMpaRatings();
    }

    @GetMapping("/{mpaRatingId}")
    public MpaRating getMpaRating(@PathVariable Long mpaRatingId) {
        return mpaRatingService.getMpaRating(mpaRatingId);
    }


}
