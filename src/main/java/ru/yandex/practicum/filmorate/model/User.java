package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.validator.LoginValidator;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"email"})
public class User {
    private Long id;

    @Email
    private String email;

    @NonNull
    @LoginValidator
    private String login;

    private String name;

    @NonNull
    @PastOrPresent
    private LocalDate birthday;
}
