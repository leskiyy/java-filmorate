package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumDateValidator.class)
public @interface MinimumDate {
    String message() default "Date must not be before {value}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default "1895-12-28";
}
