package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class Film {
    private Long id;

    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    private String description;

    @EqualsAndHashCode.Exclude
    private LocalDate releaseDate;

    @EqualsAndHashCode.Exclude
    private Integer duration;

    @EqualsAndHashCode.Exclude
    private Integer mpa;
}
