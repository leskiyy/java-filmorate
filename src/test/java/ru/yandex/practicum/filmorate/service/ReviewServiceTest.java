package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dto.EventDTO;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.yandex.practicum.filmorate.utils.ReviewMapper.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private FilmRepository filmRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventService eventService;

    @InjectMocks
    private ReviewService service;

    @Test
    void addReview() {
        Review reviewToAdd = new Review().setUserId(1L).setFilmId(1L);
        Review expected = new Review().setId(1L).setUserId(1L);
        when(reviewRepository.save(reviewToAdd)).thenReturn(expected);
        when(userRepository.existById(1L)).thenReturn(true);
        when(filmRepository.existById(1L)).thenReturn(true);

        ReviewDTO actual = service.addReview(mapToDto(reviewToAdd, 0));

        verify(filmRepository, times(1)).existById(1L);
        verify(userRepository, times(1)).existById(1L);
        verify(reviewRepository, times(1)).save(reviewToAdd);
        assertEquals(mapToDto(expected, 0), actual);
    }

    @Test
    void addReview_whenUserIsNotFound() {
        Review reviewToAdd = new Review().setUserId(1L).setFilmId(1L);
        Review expected = new Review().setId(1L);
        when(userRepository.existById(1L)).thenReturn(false);

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.addReview(mapToDto(reviewToAdd, 0)));

        verify(userRepository, times(1)).existById(1L);
        verify(filmRepository, never()).existById(1L);
        verify(reviewRepository, never()).save(reviewToAdd);
        assertEquals("There is no user with id=1", throwable.getMessage());
    }

    @Test
    void addReview_whenFilmIsNotFound() {
        Review reviewToAdd = new Review().setUserId(1L).setFilmId(1L);
        Review expected = new Review().setId(1L);
        when(userRepository.existById(1L)).thenReturn(true);
        when(filmRepository.existById(1L)).thenReturn(false);

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.addReview(mapToDto(reviewToAdd, 0)));

        verify(userRepository, times(1)).existById(1L);
        verify(filmRepository, times(1)).existById(1L);
        verify(reviewRepository, never()).save(reviewToAdd);
        assertEquals("There is no film with id=1", throwable.getMessage());
    }

    @Test
    void updateReview() {
        Review reviewToUpdate = new Review().setUserId(1L).setFilmId(1L);
        Review expected = new Review().setId(1L).setUserId(1L);
        when(reviewRepository.update(reviewToUpdate)).thenReturn(expected);
        when(userRepository.existById(1L)).thenReturn(true);
        when(filmRepository.existById(1L)).thenReturn(true);

        ReviewDTO actual = service.updateReview(mapToDto(reviewToUpdate, 0));

        verify(filmRepository, times(1)).existById(1L);
        verify(userRepository, times(1)).existById(1L);
        verify(reviewRepository, times(1)).update(reviewToUpdate);
        assertEquals(mapToDto(expected, 0), actual);
    }

    @Test
    void deleteReview_whenTrue() {
        when(reviewRepository.deleteById(1L)).thenReturn(true);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(new Review().setUserId(1L)));
        when(eventService.createReviewEvent(1L, 1L, "REMOVE")).thenReturn(new EventDTO());
        assertTrue(service.deleteReview(1L));
        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteReview_whenFalse() {
        when(reviewRepository.deleteById(1L)).thenReturn(false);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(new Review().setUserId(1L)));
        when(eventService.createReviewEvent(1L, 1L, "REMOVE")).thenReturn(new EventDTO());
        assertFalse(service.deleteReview(1L));
        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllReviews() {
        Review expected = new Review().setId(1L);
        when(reviewRepository.findAll()).thenReturn(List.of(expected));
        when(reviewRepository.calculateUsefulByReviewId(1L)).thenReturn(2);

        List<ReviewDTO> actual = service.getAllReviews();

        verify(reviewRepository, times(1)).findAll();
        verify(reviewRepository, times(1)).calculateUsefulByReviewId(1L);
        assertEquals(1, actual.size());
        assertEquals(mapToDto(expected, 2), actual.getFirst());
    }

    @Test
    void getReviewsByFilmId() {
        Review expected = new Review().setId(1L);
        when(reviewRepository.getReviewsByFilmId(1L, 10)).thenReturn(List.of(expected));
        when(reviewRepository.calculateUsefulByReviewId(1L)).thenReturn(2);
        when(filmRepository.existById(1L)).thenReturn(true);

        List<ReviewDTO> actual = service.getReviewsByFilmId(1L, 10);

        verify(reviewRepository, times(1)).getReviewsByFilmId(1L, 10);
        verify(reviewRepository, times(1)).calculateUsefulByReviewId(1L);
        verify(filmRepository, times(1)).existById(1L);
        assertEquals(1, actual.size());
        assertEquals(mapToDto(expected, 2), actual.getFirst());
    }

    @Test
    void addReviewLike_whenTrue() {
        when(reviewRepository.addReviewLike(1L, 1L)).thenReturn(true);
        when(reviewRepository.existById(1L)).thenReturn(true);
        when(userRepository.existById(1L)).thenReturn(true);

        assertTrue(service.addReviewLike(1L, 1L));
        verify(reviewRepository, times(1)).addReviewLike(1L, 1L);
        verify(reviewRepository, times(1)).existById(1L);
        verify(userRepository, times(1)).existById(1L);
    }

    @Test
    void addReviewLike_whenFalse() {
        when(reviewRepository.addReviewLike(1L, 1L)).thenReturn(false);
        when(reviewRepository.existById(1L)).thenReturn(true);
        when(userRepository.existById(1L)).thenReturn(true);

        assertFalse(service.addReviewLike(1L, 1L));
        verify(reviewRepository, times(1)).addReviewLike(1L, 1L);
        verify(reviewRepository, times(1)).existById(1L);
        verify(userRepository, times(1)).existById(1L);
    }

    @Test
    void addReviewDislike_whenTrue() {
        when(reviewRepository.addReviewDislike(1L, 1L)).thenReturn(true);
        when(reviewRepository.existById(1L)).thenReturn(true);
        when(userRepository.existById(1L)).thenReturn(true);

        assertTrue(service.addReviewDislike(1L, 1L));
        verify(reviewRepository, times(1)).addReviewDislike(1L, 1L);
        verify(reviewRepository, times(1)).existById(1L);
        verify(userRepository, times(1)).existById(1L);
    }

    @Test
    void addReviewDislike_whenFalse() {
        when(reviewRepository.addReviewDislike(1L, 1L)).thenReturn(false);
        when(reviewRepository.existById(1L)).thenReturn(true);
        when(userRepository.existById(1L)).thenReturn(true);

        assertFalse(service.addReviewDislike(1L, 1L));
        verify(reviewRepository, times(1)).addReviewDislike(1L, 1L);
        verify(reviewRepository, times(1)).existById(1L);
        verify(userRepository, times(1)).existById(1L);
    }

    @Test
    void deleteReviewLikeOrDislike_whenTrue() {
        when(reviewRepository.deleteLikeDislikeRow(1L, 1L)).thenReturn(true);
        when(reviewRepository.existById(1L)).thenReturn(true);
        when(userRepository.existById(1L)).thenReturn(true);

        assertTrue(service.deleteReviewLikeOrDislike(1L, 1L));
        verify(reviewRepository, times(1)).deleteLikeDislikeRow(1L, 1L);
        verify(reviewRepository, times(1)).existById(1L);
        verify(userRepository, times(1)).existById(1L);
    }

    @Test
    void deleteReviewLikeOrDislike_whenFalse() {
        when(reviewRepository.deleteLikeDislikeRow(1L, 1L)).thenReturn(false);
        when(reviewRepository.existById(1L)).thenReturn(true);
        when(userRepository.existById(1L)).thenReturn(true);

        assertFalse(service.deleteReviewLikeOrDislike(1L, 1L));
        verify(reviewRepository, times(1)).deleteLikeDislikeRow(1L, 1L);
        verify(reviewRepository, times(1)).existById(1L);
        verify(userRepository, times(1)).existById(1L);
    }

    @Test
    void deleteReviewDislike_whenTrue() {
        when(reviewRepository.deleteDislikeRow(1L, 1L)).thenReturn(true);
        when(reviewRepository.existById(1L)).thenReturn(true);
        when(userRepository.existById(1L)).thenReturn(true);

        assertTrue(service.deleteReviewDislike(1L, 1L));
        verify(reviewRepository, times(1)).deleteDislikeRow(1L, 1L);
        verify(reviewRepository, times(1)).existById(1L);
        verify(userRepository, times(1)).existById(1L);
    }

    @Test
    void deleteReviewDislike_whenFalse() {
        when(reviewRepository.deleteDislikeRow(1L, 1L)).thenReturn(false);
        when(reviewRepository.existById(1L)).thenReturn(true);
        when(userRepository.existById(1L)).thenReturn(true);

        assertFalse(service.deleteReviewDislike(1L, 1L));
        verify(reviewRepository, times(1)).deleteDislikeRow(1L, 1L);
        verify(reviewRepository, times(1)).existById(1L);
        verify(userRepository, times(1)).existById(1L);
    }
}