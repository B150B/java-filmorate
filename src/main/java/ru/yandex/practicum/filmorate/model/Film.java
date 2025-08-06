package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.ValidReleaseDate;

import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200, message = "Описание не должно быть длиннее 200 символов")
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private Set<Long> likedUserIds;

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
