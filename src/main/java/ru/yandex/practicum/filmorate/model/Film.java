package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.MinimumDate;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    @EqualsAndHashCode.Exclude
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Size(max = 200, message = "Max description length is 200 characters")
    @EqualsAndHashCode.Exclude
    private String description;

    @NotNull
    @MinimumDate
    @PastOrPresent(message = "release date can't be in future")
    private LocalDate releaseDate;

    @NotNull
    @Positive
    private Integer duration;

    @EqualsAndHashCode.Exclude
    private Integer rate;

}