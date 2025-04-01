package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.yandex.practicum.filmorate.validator.LoginValidator;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"email"})
@Accessors(chain = true)
public class User {
    private Long id;

    @Email
    private String email;

    @NotNull
    @LoginValidator
    private String login;

    private String name;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;

}
