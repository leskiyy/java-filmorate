package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validator.MinimumDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Film.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FilmDTO {

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

    @NotNull
    private Mpa mpa;

    private List<Genre> genres;
    private List<Director> directors = new ArrayList<>();

}