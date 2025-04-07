package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService service;

    @InjectMocks
    private ReviewController controller;

    @Test
    void addReview() {
        ReviewDTO expected = new ReviewDTO();
        ReviewDTO reviewToAdd = new ReviewDTO();
        when(service.addReview(reviewToAdd)).thenReturn(expected);

        ReviewDTO actual = controller.addReview(reviewToAdd);

        verify(service, times(1)).addReview(reviewToAdd);
        assertSame(expected, actual);
    }

    @Test
    void updateReview_whenReviewIsFound() {
        ReviewDTO expected = new ReviewDTO();
        ReviewDTO reviewToUpdate = new ReviewDTO();
        when(service.updateReview(reviewToUpdate)).thenReturn(expected);

        ReviewDTO actual = controller.updateReview(reviewToUpdate);

        verify(service, times(1)).updateReview(reviewToUpdate);
        assertSame(expected, actual);
    }

    @Test
    void updateReview_whenReviewIsNotFound() {
        ReviewDTO reviewToUpdate = new ReviewDTO();
        when(service.updateReview(reviewToUpdate)).thenThrow(new NotFoundException("test"));

        Throwable throwable = assertThrows(NotFoundException.class, () -> controller.updateReview(reviewToUpdate));

        verify(service, times(1)).updateReview(reviewToUpdate);
        assertEquals("test", throwable.getMessage());
    }

    @Test
    void deleteReview_whenFalse() {
        when(service.deleteReview(1L)).thenReturn(false);

        Map<String, String> response = controller.deleteReview(1L);

        verify(service, times(1)).deleteReview(1L);
        assertEquals("Review id=1 can't be deleted", response.get("FAIL"));
    }

    @Test
    void deleteReview_whenTrue() {
        when(service.deleteReview(1L)).thenReturn(true);

        Map<String, String> response = controller.deleteReview(1L);

        verify(service, times(1)).deleteReview(1L);
        assertEquals("Review id=1 was deleted", response.get("SUCCESS"));
    }

    @Test
    void getReviewsByParams_whenFilmIdEqualsNull() {
        ReviewDTO expected = new ReviewDTO();
        List<ReviewDTO> allReviews = List.of(expected);
        when(service.getAllReviews()).thenReturn(allReviews);

        List<ReviewDTO> actual = controller.getReviewsByParams(null, 10);

        verify(service, times(1)).getAllReviews();
        assertEquals(allReviews.size(), actual.size());
        assertSame(expected, actual.getFirst());
    }

    @Test
    void getReviewsByParams_whenFilmIdEqualsNotNull() {
        ReviewDTO expected = new ReviewDTO();
        List<ReviewDTO> allReviews = List.of(expected);
        when(service.getReviewsByFilmId(1L, 10)).thenReturn(allReviews);

        List<ReviewDTO> actual = controller.getReviewsByParams(1L, 10);

        verify(service, times(1)).getReviewsByFilmId(1L, 10);
        assertEquals(allReviews.size(), actual.size());
        assertSame(expected, actual.getFirst());
    }

    @Test
    void addLikeToReview_whenTrue() {
        when(service.addReviewLike(1L, 1L)).thenReturn(true);

        Map<String, String> response = controller.addLikeToReview(1L, 1L);

        verify(service, times(1)).addReviewLike(1L, 1L);
        assertEquals("Like to review id=1 was added by user id=1", response.get("SUCCESS"));
    }

    @Test
    void addLikeToReview_whenFalse() {
        when(service.addReviewLike(1L, 1L)).thenReturn(false);

        Map<String, String> response = controller.addLikeToReview(1L, 1L);

        verify(service, times(1)).addReviewLike(1L, 1L);
        assertEquals("Like to review id=1 can't be added by user id=1", response.get("FAIL"));
    }

    @Test
    void addDislikeToReview_whenTrue() {
        when(service.addReviewDislike(1L, 1L)).thenReturn(true);

        Map<String, String> response = controller.addDislikeToReview(1L, 1L);

        verify(service, times(1)).addReviewDislike(1L, 1L);
        assertEquals("Dislike to review id=1 was added by user id=1", response.get("SUCCESS"));
    }

    @Test
    void addDislikeToReview_whenFalse() {
        when(service.addReviewDislike(1L, 1L)).thenReturn(false);

        Map<String, String> response = controller.addDislikeToReview(1L, 1L);

        verify(service, times(1)).addReviewDislike(1L, 1L);
        assertEquals("Dislike to review id=1 can't be added by user id=1", response.get("FAIL"));
    }

    @Test
    void deleteReviewLikeOrDislike_whenTrue() {
        when(service.deleteReviewLikeOrDislike(1L, 1L)).thenReturn(true);

        Map<String, String> response = controller.deleteReviewLikeOrDislike(1L, 1L);

        verify(service, times(1)).deleteReviewLikeOrDislike(1L, 1L);
        assertEquals("Like or dislike to review id=1 was deleted by user id=1", response.get("SUCCESS"));
    }

    @Test
    void deleteReviewLikeOrDislike_whenFalse() {
        when(service.deleteReviewLikeOrDislike(1L, 1L)).thenReturn(false);

        Map<String, String> response = controller.deleteReviewLikeOrDislike(1L, 1L);

        verify(service, times(1)).deleteReviewLikeOrDislike(1L, 1L);
        assertEquals("Like or dislike to review id=1 can't be deleted by user id=1", response.get("FAIL"));
    }

    @Test
    void deleteReviewDislike_whenTrue() {
        when(service.deleteReviewDislike(1L, 1L)).thenReturn(true);

        Map<String, String> response = controller.deleteReviewDislike(1L, 1L);

        verify(service, times(1)).deleteReviewDislike(1L, 1L);
        assertEquals("Dislike to review id=1 was deleted by user id=1", response.get("SUCCESS"));
    }

    @Test
    void deleteReviewDislike_whenFalse() {
        when(service.deleteReviewDislike(1L, 1L)).thenReturn(false);

        Map<String, String> response = controller.deleteReviewDislike(1L, 1L);

        verify(service, times(1)).deleteReviewDislike(1L, 1L);
        assertEquals("Dislike to review id=1 can't be deleted by user id=1", response.get("FAIL"));
    }
}