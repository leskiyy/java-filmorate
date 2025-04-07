package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.utils.BooleanAnswerBuilder.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService service;

    @PostMapping
    public ReviewDTO addReview(@RequestBody Review review) {
        ReviewDTO addedReview = service.addReview(review);
        log.info("Successfully add review {}", review);
        return addedReview;
    }

    @PutMapping
    public ReviewDTO updateReview(@RequestBody Review review) {
        ReviewDTO updatedReview = service.updateReview(review);
        log.info("Successfully update review {}", review);
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteReview(@PathVariable long id) {
        boolean isSuccess = service.deleteReview(id);
        return isSuccess ? deleteReviewSuccessAnswer(id) : deleteReviewFailAnswer(id);
    }

    @GetMapping("/{id}")
    public ReviewDTO getReviewById(@PathVariable Long id) {
        ReviewDTO review = service.getReviewsById(id);
        log.info("Successfully get review {}", review);
        return review;
    }

    @GetMapping
    public List<ReviewDTO> getReviewsByParams(@RequestParam(required = false) Long filmId,
                                              @RequestParam(defaultValue = "10") Integer count) {
        List<ReviewDTO> response;
        if (filmId == null) {
            response = service.getAllReviews();
            log.info("Successfully get all reviews");
        } else {
            response = service.getReviewsByFilmId(filmId, count);
            log.info("Successfully get {} reviews {} of film with id=", count, filmId);
        }
        return response;
    }

    @PutMapping("/{id}/like/{userId}")
    public Map<String, String> addLikeToReview(@PathVariable long id, @PathVariable long userId) {
        boolean isSuccess = service.addReviewLike(id, userId);
        return isSuccess ? addLikeToReviewSuccessAnswer(id, userId) : addLikeToReviewFailAnswer(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Map<String, String> addDislikeToReview(@PathVariable long id, @PathVariable long userId) {
        boolean isSuccess = service.addReviewDislike(id, userId);
        return isSuccess ? addDislikeToReviewSuccessAnswer(id, userId) : addDislikeToReviewFailAnswer(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Map<String, String> deleteReviewLikeOrDislike(@PathVariable long id, @PathVariable long userId) {
        boolean isSuccess = service.deleteReviewLikeOrDislike(id, userId);
        return isSuccess ?
                deleteReviewLikeOrDislikeSuccessAnswer(id, userId) : deleteReviewLikeOrDislikeFailAnswer(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Map<String, String> deleteReviewDislike(@PathVariable long id, @PathVariable long userId) {
        boolean isSuccess = service.deleteReviewDislike(id, userId);
        return isSuccess ? deleteReviewDislikeSuccessAnswer(id, userId) : deleteReviewDislikeFailAnswer(id, userId);
    }
}
