package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationFilmTest {

    private static final ValidatorFactory factory;
    private static final Validator validator;

    private static final FilmDTO validFilm;
    private static final FilmDTO invalidNameFilm;
    private static final FilmDTO invalidDescriptionFilm;
    private static final FilmDTO invalidMinimumDateFilm;
    private static final FilmDTO invalidFutureDateFilm;
    private static final FilmDTO invalidDurationDateFilm;

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

        validFilm = new FilmDTO(0L, NAME, DESCRIPTION, DATE, 1, 0, new Mpa(), null, new ArrayList<>());
        invalidNameFilm = new FilmDTO(0L, INVALID_NAME, DESCRIPTION, DATE, 1, 0, new Mpa(), null, new ArrayList<>());
        invalidDescriptionFilm = new FilmDTO(0L, NAME, INVALID_DESCRIPTION, DATE, 1, 0, new Mpa(), null, new ArrayList<>());
        invalidMinimumDateFilm = new FilmDTO(0L, NAME, DESCRIPTION, INVALID_PAST_DATE, 1, 0, new Mpa(), null, new ArrayList<>());
        invalidFutureDateFilm = new FilmDTO(0L, NAME, DESCRIPTION, INVALID_FUTURE_DATE, 1, 0, new Mpa(), null, new ArrayList<>());
        invalidDurationDateFilm = new FilmDTO(0L, NAME, DESCRIPTION, DATE, -1, 0, new Mpa(), null, new ArrayList<>());
    }

    @Test
    void validateFilm() {
        Set<ConstraintViolation<FilmDTO>> constraintViolations = validator.validate(validFilm);

        assertFalse(constraintViolations.iterator().hasNext());
    }

    @Test
    void validateInvalidNameFilm() {
        Set<ConstraintViolation<FilmDTO>> constraintViolations = validator.validate(invalidNameFilm);
        Iterator<ConstraintViolation<FilmDTO>> iterator = constraintViolations.iterator();

        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("must not be blank", iterator.next().getMessage());
    }

    @Test
    void validateInvalidDescriptionFilm() {
        Set<ConstraintViolation<FilmDTO>> constraintViolations = validator.validate(invalidDescriptionFilm);
        Iterator<ConstraintViolation<FilmDTO>> iterator = constraintViolations.iterator();

        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("Max description length is 200 characters", iterator.next().getMessage());
    }

    @Test
    void validateInvalidFutureDateFilm() {
        Set<ConstraintViolation<FilmDTO>> constraintViolations = validator.validate(invalidFutureDateFilm);
        Iterator<ConstraintViolation<FilmDTO>> iterator = constraintViolations.iterator();

        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("release date can't be in future", iterator.next().getMessage());
    }

    @Test
    void validateInvalidPastDateFilm() {
        Set<ConstraintViolation<FilmDTO>> constraintViolations = validator.validate(invalidMinimumDateFilm);
        Iterator<ConstraintViolation<FilmDTO>> iterator = constraintViolations.iterator();

        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("Date must not be before 1895-12-28", iterator.next().getMessage());
    }

    @Test
    void validateInvalidDurationFilm() {
        Set<ConstraintViolation<FilmDTO>> constraintViolations = validator.validate(invalidDurationDateFilm);
        Iterator<ConstraintViolation<FilmDTO>> iterator = constraintViolations.iterator();

        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("must be greater than 0", iterator.next().getMessage());
    }
}
