package ru.yandex.practicum.filmorate.utils;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.model.Review;

@UtilityClass
public class ReviewMapper {
    public ReviewDTO mapToDto(Review review, int useful) {
        return new ReviewDTO()
                .setReviewId(review.getId())
                .setContent(review.getContent())
                .setIsPositive(review.getIsPositive())
                .setUserId(review.getUserId())
                .setFilmId(review.getFilmId())
                .setUseful(useful);
    }

    public Review mapToReview(ReviewDTO dto) {
        return new Review()
                .setId(dto.getReviewId())
                .setContent(dto.getContent())
                .setIsPositive(dto.getIsPositive())
                .setFilmId(dto.getFilmId())
                .setUserId(dto.getUserId());
    }
}
