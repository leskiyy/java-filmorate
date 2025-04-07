package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dto.EventDTO;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.repository.EventRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventDTO> getUserFeed(@Positive long userId) {
        return eventRepository.getByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getAllEvents() {
        return eventRepository.getAllEvents().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public EventDTO createEvent(EventDTO eventDto) {
        Event event = convertToEntity(eventDto);
        validateEvent(event);
        Event createdEvent = eventRepository.create(event);
        log.info("Created event: {}", createdEvent);
        return convertToDto(createdEvent);
    }

    private void validateEvent(Event event) {
        if (!Set.of("LIKE", "REVIEW", "FRIEND").contains(event.getEventType())) {
            throw new ValidationException("Invalid event type");
        }
        if (!Set.of("ADD", "REMOVE", "UPDATE").contains(event.getOperation())) {
            throw new ValidationException("Invalid operation");
        }
    }

    public EventDTO createFriendEvent(long userId, long friendId, String operation) {
        EventDTO eventDto = new EventDTO();
        eventDto.setUserId(userId);
        eventDto.setEventType("FRIEND");
        eventDto.setOperation(operation);
        eventDto.setEntityId(userId);
        return createEvent(eventDto);
    }

    public EventDTO createLikeEvent(long userId, long filmId, String operation) {
        EventDTO eventDto = new EventDTO();
        eventDto.setUserId(userId);
        eventDto.setEventType("LIKE");
        eventDto.setOperation(operation);
        eventDto.setEntityId(filmId);
        return createEvent(eventDto);
    }

    public EventDTO createReviewEvent(long userId, long reviewId, String operation) {
        EventDTO eventDto = new EventDTO();
        eventDto.setUserId(userId);
        eventDto.setEventType("REVIEW");
        eventDto.setOperation(operation);
        eventDto.setEntityId(reviewId);
        return createEvent(eventDto);
    }

    public EventDTO convertToDto(Event event) {
        long timestamp = event.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        return new EventDTO(
                timestamp,
                event.getUserId(),
                event.getEventType(),
                event.getOperation(),
                event.getEventId(),
                event.getEntityId()
        );
    }

    private Event convertToEntity(EventDTO eventDto) {
        LocalDateTime createdAt = eventDto.getTimestamp() != null
                ? Instant.ofEpochMilli(eventDto.getTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                : LocalDateTime.now();

        return Event.builder()
                .eventId(eventDto.getEventId())
                .userId(eventDto.getUserId())
                .eventType(eventDto.getEventType())
                .operation(eventDto.getOperation())
                .entityId(eventDto.getEntityId())
                .createdAt(createdAt)
                .build();
    }
}
