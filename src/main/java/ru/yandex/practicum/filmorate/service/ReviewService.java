package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.utils.ReviewMapper.*;

@Service
@Validated
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final EventService eventService;
    private static final String METHOD_ADD = "ADD";
    private static final String METHOD_DELETE = "REMOVE";
    private static final String METHOD_UPDATE = "UPDATE";

    public ReviewDTO getReviewsById(@NotNull Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isEmpty()) {
            throw new NotFoundException("There is no review with id=" + id);
        } else {
            return mapToDto(optionalReview.get(), reviewRepository.calculateUsefulByReviewId(id));
        }
    }

    public ReviewDTO addReview(@Valid ReviewDTO review) {
        validateUserByUserId(review.getUserId());
        validateFilmByFilmId(review.getFilmId());
        Review save = reviewRepository.save(mapToReview(review));
        eventService.createReviewEvent(save.getUserId(), save.getId(), METHOD_ADD);
        return mapToDto(save, 0);
    }

    public ReviewDTO updateReview(@Valid ReviewDTO review) {
        validateUserByUserId(review.getUserId());
        validateFilmByFilmId(review.getFilmId());
        Review update = reviewRepository.update(mapToReview(review));
        eventService.createReviewEvent(update.getUserId(), update.getId(), METHOD_UPDATE);
        return mapToDto(update, reviewRepository.calculateUsefulByReviewId(update.getId()));
    }

    public boolean deleteReview(@Positive long id) {
        ReviewDTO reviewDTO = getReviewsById(id);
        eventService.createReviewEvent(reviewDTO.getUserId(), id, METHOD_DELETE);
        return reviewRepository.deleteById(id);
    }

    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(review -> mapToDto(review, reviewRepository.calculateUsefulByReviewId(review.getId())))
                .toList();
    }


    public List<ReviewDTO> getReviewsByFilmId(@Positive Long filmId, @Positive Integer count) {
        validateFilmByFilmId(filmId);
        return reviewRepository.getReviewsByFilmId(filmId, count).stream()
                .map(review -> mapToDto(review, reviewRepository.calculateUsefulByReviewId(review.getId())))
                .toList();
    }

    public boolean addReviewLike(@Positive long id, @Positive long userId) {
        validateReviewByReviewId(id);
        validateUserByUserId(userId);
        return reviewRepository.addReviewLike(id, userId);
    }

    public boolean addReviewDislike(@Positive long id, @Positive long userId) {
        validateReviewByReviewId(id);
        validateUserByUserId(userId);
        return reviewRepository.addReviewDislike(id, userId);
    }

    public boolean deleteReviewLikeOrDislike(@Positive long id, @Positive long userId) {
        validateReviewByReviewId(id);
        validateUserByUserId(userId);
        return reviewRepository.deleteLikeDislikeRow(id, userId);
    }

    public boolean deleteReviewDislike(@Positive long id, @Positive long userId) {
        validateReviewByReviewId(id);
        validateUserByUserId(userId);
        return reviewRepository.deleteDislikeRow(id, userId);
    }

    private void validateUserByUserId(long userId) {
        if (!userRepository.existById(userId)) throw new NotFoundException("There is no user with id=" + userId);
    }

    private void validateFilmByFilmId(long filmId) {
        if (!filmRepository.existById(filmId)) throw new NotFoundException("There is no film with id=" + filmId);
    }

    private void validateReviewByReviewId(long reviewId) {
        if (!reviewRepository.existById(reviewId))
            throw new NotFoundException("There is no review with id=" + reviewId);
    }
}
