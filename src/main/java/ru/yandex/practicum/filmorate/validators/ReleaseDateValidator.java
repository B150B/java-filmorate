package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {
    private static final LocalDate BEGIN_DATE = LocalDate.of(1985, 12, 28);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext context) {
        if (localDate == null) {
            return true;
        }
        return !localDate.isBefore(BEGIN_DATE);
    }

}
