package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StringWithoutSpacesValidator.class)
public @interface LoginValidator {
    String message() default "Login must be without space character and must not be blank";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

}
