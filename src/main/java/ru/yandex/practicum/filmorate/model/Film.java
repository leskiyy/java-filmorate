package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.validator.MinimumDate;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@AllArgsConstructor
public class Film {

    @EqualsAndHashCode.Exclude
    private Long id;

    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @Size(max = 200, message = "Max description length is 200 characters")
    @EqualsAndHashCode.Exclude
    private String description;

    @NonNull
    @MinimumDate
    @PastOrPresent(message = "release date can't be in future")
    private LocalDate releaseDate;

    @NonNull
    @Positive
    private Integer duration;
}