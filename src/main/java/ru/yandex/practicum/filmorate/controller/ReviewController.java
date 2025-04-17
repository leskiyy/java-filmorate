package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService service;
    private final EventService eventService;

    @PostMapping
    public ReviewDTO addReview(@RequestBody ReviewDTO review) {
        ReviewDTO addedReview = service.addReview(review);
        log.info("Successfully add review id={}", review.getReviewId());
        return addedReview;
    }

    @PutMapping
    public ReviewDTO updateReview(@RequestBody ReviewDTO review) {
        ReviewDTO updatedReview = service.updateReview(review);
        log.info("Successfully update review id={}", review.getReviewId());
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        service.deleteReview(id);
    }

    @GetMapping("/{id}")
    public ReviewDTO getReviewById(@PathVariable long id) {
        ReviewDTO review = service.getReviewsById(id);
        log.info("Successfully get review {}", review);
        return review;
    }

    @GetMapping
    public List<ReviewDTO> getReviewsByParams(@RequestParam(required = false) Long filmId,
                                              @RequestParam(required = false) Integer count) {
        List<ReviewDTO> response;
        if (filmId == null) {
            response = service.getAllReviews();
            log.info("Successfully get all reviews");
        } else {
            if (count == null) {
                count = 10;
            }
            response = service.getReviewsByFilmId(filmId, count);
            log.info("Successfully get {} reviews of film with id={}", response.size(), filmId);
        }
        return response;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable long id, @PathVariable long userId) {
        service.addReviewLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable long id, @PathVariable long userId) {
        service.addReviewDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteReviewLikeOrDislike(@PathVariable long id, @PathVariable long userId) {
        service.deleteReviewLikeOrDislike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteReviewDislike(@PathVariable long id, @PathVariable long userId) {
        service.deleteReviewDislike(id, userId);
    }
}
