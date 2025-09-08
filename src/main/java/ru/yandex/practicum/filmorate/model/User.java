package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.ValidBirthDay;

import java.time.LocalDate;
import java.util.HashSet;
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
    @ValidBirthDay
    private LocalDate birthday;
    private Set<Long> friendsIds;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        if ((name == null) || (name.isEmpty())) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
        this.friendsIds = new HashSet<>();
    }


}
