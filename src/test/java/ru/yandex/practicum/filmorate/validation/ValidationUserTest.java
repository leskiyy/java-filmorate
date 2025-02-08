package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUserTest {
    private static final ValidatorFactory factory;
    private static final Validator validator;

    private static final User validUser;
    private static final User invalidEmailUser;
    private static final User invalidBlankLoginUser;
    private static final User invalidWithSpaceLoginUser;
    private static final User invalidBirthdayUser;

    public static final String EMAIL = "mail@mail.com";
    public static final String INVALID_EMAIL = "mail.com";
    public static final String LOGIN = "login";
    public static final String INVALID_BLANK_LOGIN = "";
    public static final String INVALID_WITH_SPACE_LOGIN = "lo gin";
    public static final String NAME = "name";
    public static final LocalDate BIRTHDAY = LocalDate.of(2000, 1, 1);
    public static final LocalDate INVALID_BIRTHDAY = LocalDate.of(2050, 1, 1);

    static {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validUser = new User(0L, EMAIL, LOGIN, NAME, BIRTHDAY);
        invalidEmailUser = new User(0L, INVALID_EMAIL, LOGIN, NAME, BIRTHDAY);
        invalidBlankLoginUser = new User(0L, EMAIL, INVALID_BLANK_LOGIN, NAME, BIRTHDAY);
        invalidWithSpaceLoginUser = new User(0L, EMAIL, INVALID_WITH_SPACE_LOGIN, NAME, BIRTHDAY);
        invalidBirthdayUser = new User(0L, EMAIL, LOGIN, NAME, INVALID_BIRTHDAY);
    }

    @Test
    void validateUser() {
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(validUser);

        assertFalse(constraintViolations.iterator().hasNext());
    }

    @Test
    void validateInvalidEmailUser() {
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(invalidEmailUser);
        Iterator<ConstraintViolation<User>> iterator = constraintViolations.iterator();


        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("must be a well-formed email address", iterator.next().getMessage());
    }

    @Test
    void validateInvalidBlankLoginUser() {
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(invalidBlankLoginUser);
        Iterator<ConstraintViolation<User>> iterator = constraintViolations.iterator();


        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("Login must be without space character and must not be blank", iterator.next().getMessage());
    }

    @Test
    void validateInvalidWithSpaceLoginUser() {
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(invalidWithSpaceLoginUser);
        Iterator<ConstraintViolation<User>> iterator = constraintViolations.iterator();


        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("Login must be without space character and must not be blank", iterator.next().getMessage());
    }

    @Test
    void validateInvalidBirthdayUser() {
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(invalidBirthdayUser);
        Iterator<ConstraintViolation<User>> iterator = constraintViolations.iterator();


        assertTrue(constraintViolations.iterator().hasNext());
        assertEquals("must be a date in the past or in the present", iterator.next().getMessage());
    }
}
