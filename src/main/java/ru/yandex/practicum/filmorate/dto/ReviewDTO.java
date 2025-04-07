package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;

    @NotNull
    @NotBlank
    private String content;

    @NotNull
    private Boolean isPositive;

    @NotNull
    @Positive
    private Long userId;

    @NotNull
    @Positive
    private Long filmId;

    private Integer useful;
}
