package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationFilmTest {

    private static final ValidatorFactory factory;
    private static final Validator validator;

    private static final Film validFilm;
    private static final Film invalidNameFilm;
    private static final Film invalidDescriptionFilm;
    private static final Film invalidMinimumDateFilm;
    private static final Film invalidFutureDateFilm;
    private static final Film invalidDurationDateFilm;

    private static final String NAME = "name";
    private static final String INVALID_NAME = "";
    private static final String DESCRIPTION = "description";
    private static final String INVALID_DESCRIPTION = new String(new char[201]);
    private static final LocalDate DATE = LocalDate.of(2025, 2, 6);
    private static final LocalDate INVALID_FUTURE_DATE = LocalDate.of(2030, 1, 1);
    private static final LocalDate INVALID_PAST_DATE = LocalDate.of(1888, 1, 1);

    static {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validFilm = new Film(0L, NAME, DESCRIPTION, DATE, 1);
        invalidNameFilm = new Film(0L, INVALID_NAME, DESCRIPTION, DATE, 1);
        invalidDescriptionFilm = new Film(0L, NAME, INVALID_DESCRIPTION, DATE, 1);
        invalidMinimumDateFilm = new Film(0L, NAME, DESCRIPTION, INVALID_PAST_DATE, 1);
        invalidFutureDateFilm = new Film(0L, NAME, DESCRIPTION, INVALID_FUTURE_DATE, 1);
        invalidDurationDateFilm = new Film(0L, NAME, DESCRIPTION, DATE, -1);
    }

    @Test
    void validateFilm() {
        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(validFilm);

        assertFalse(constraintViolations.iterator().hasNext());
    }

    @Test
    void validateInvalidNameFilm() {
        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(invalidNameFilm);
        Iterator<ConstraintViolation<Film>> iterator = constraintViolations.iterator();

        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("must not be blank", iterator.next().getMessage());
    }

    @Test
    void validateInvalidDescriptionFilm() {
        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(invalidDescriptionFilm);
        Iterator<ConstraintViolation<Film>> iterator = constraintViolations.iterator();

        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("Max description length is 200 characters", iterator.next().getMessage());
    }

    @Test
    void validateInvalidFutureDateFilm() {
        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(invalidFutureDateFilm);
        Iterator<ConstraintViolation<Film>> iterator = constraintViolations.iterator();

        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("release date can't be in future", iterator.next().getMessage());
    }

    @Test
    void validateInvalidPastDateFilm() {
        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(invalidMinimumDateFilm);
        Iterator<ConstraintViolation<Film>> iterator = constraintViolations.iterator();

        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("Date must not be before 1895-12-28", iterator.next().getMessage());
    }

    @Test
    void validateInvalidDurationFilm() {
        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(invalidDurationDateFilm);
        Iterator<ConstraintViolation<Film>> iterator = constraintViolations.iterator();

        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("must be greater than 0", iterator.next().getMessage());
    }
}
