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
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.utils.OperationType;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.utils.ReviewMapper.*;

@Service
@Validated
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    private final EventService eventService;
    private final ValidationService validationService;

    public ReviewDTO getReviewsById(@NotNull Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isEmpty()) {
            throw new NotFoundException("There is no review with id=" + id);
        } else {
            return mapToDto(optionalReview.get(), reviewRepository.calculateUsefulByReviewId(id));
        }
    }

    public ReviewDTO addReview(@Valid ReviewDTO review) {
        validationService.validateUserById(review.getUserId());
        validationService.validateFilmById(review.getFilmId());
        Review save = reviewRepository.save(mapToReview(review));
        eventService.createReviewEvent(save.getUserId(), save.getId(), OperationType.ADD);
        return mapToDto(save, 0);
    }

    public ReviewDTO updateReview(@Valid ReviewDTO review) {
        validationService.validateUserById(review.getUserId());
        validationService.validateFilmById(review.getFilmId());
        Review update = reviewRepository.update(mapToReview(review));
        eventService.createReviewEvent(update.getUserId(), update.getId(), OperationType.UPDATE);
        return mapToDto(update, reviewRepository.calculateUsefulByReviewId(update.getId()));
    }

    public boolean deleteReview(@Positive long id) {
        ReviewDTO reviewDTO = getReviewsById(id);
        eventService.createReviewEvent(reviewDTO.getUserId(), id, OperationType.REMOVE);
        return reviewRepository.deleteById(id);
    }

    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(review -> mapToDto(review, reviewRepository.calculateUsefulByReviewId(review.getId())))
                .sorted(Comparator.comparingInt(ReviewDTO::getUseful).reversed())
                .toList();
    }


    public List<ReviewDTO> getReviewsByFilmId(@Positive Long filmId, @Positive Integer count) {
        validationService.validateFilmById(filmId);
        return reviewRepository.getReviewsByFilmId(filmId, count).stream()
                .map(review -> mapToDto(review, reviewRepository.calculateUsefulByReviewId(review.getId())))
                .sorted(Comparator.comparingInt(ReviewDTO::getUseful).reversed())
                .toList();
    }

    public boolean addReviewLike(@Positive long id, @Positive long userId) {
        validationService.validateReviewById(id);
        validationService.validateUserById(userId);
        return reviewRepository.addReviewLike(id, userId);
    }

    public boolean addReviewDislike(@Positive long id, @Positive long userId) {
        validationService.validateReviewById(id);
        validationService.validateUserById(userId);
        return reviewRepository.addReviewDislike(id, userId);
    }

    public boolean deleteReviewLikeOrDislike(@Positive long id, @Positive long userId) {
        validationService.validateReviewById(id);
        validationService.validateUserById(userId);
        return reviewRepository.deleteLikeDislikeRow(id, userId);
    }

    public boolean deleteReviewDislike(@Positive long id, @Positive long userId) {
        validationService.validateReviewById(id);
        validationService.validateUserById(userId);
        return reviewRepository.deleteDislikeRow(id, userId);
    }

}
