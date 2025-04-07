package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> getUserFeed(@Positive long userId) {
        return eventRepository.getByUserId(userId);
    }

    public List<Event> getAllEvents() {
        return eventRepository.getAllEvents();
    }

    public Event createEvent(Event event) {
        validateEvent(event);
        return eventRepository.create(event);
    }

    private void validateEvent(Event event) {
        if (!Set.of("LIKE", "REVIEW", "FRIEND").contains(event.getEventType())) {
            throw new ValidationException("Invalid event type");
        }
        if (!Set.of("ADD", "REMOVE", "UPDATE").contains(event.getOperation())) {
            throw new ValidationException("Invalid operation");
        }
    }

    public Event createFriendEvent(long userId, long friendId, String operation) {
        return createEvent(Event.builder()
                .userId(userId)
                .eventType("FRIEND")
                .operation(operation)
                .entityId(friendId)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public Event createLikeEvent(long userId, long filmId, String operation) {
        return createEvent(Event.builder()
                .userId(userId)
                .eventType("LIKE")
                .operation(operation)
                .entityId(filmId)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public Event createReviewEvent(long userId, long reviewId, String operation) {
        return createEvent(Event.builder()
                .userId(userId)
                .eventType("REVIEW")
                .operation(operation)
                .entityId(reviewId)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
