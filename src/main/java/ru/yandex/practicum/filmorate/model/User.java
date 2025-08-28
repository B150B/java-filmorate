package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "\\S+", message = "Поле не должно содержать пробелов")
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Friendship> friendships;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        if ((name == null) || (name.isEmpty())) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
    }
}
