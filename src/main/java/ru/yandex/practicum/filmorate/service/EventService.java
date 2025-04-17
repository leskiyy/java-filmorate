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
import ru.yandex.practicum.filmorate.utils.EventType;
import ru.yandex.practicum.filmorate.utils.OperationType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ValidationService validationService;

    public List<EventDTO> getUserFeed(@Positive long userId) {
        validationService.validateUserById(userId);
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
        try {
            EventType.valueOf(event.getEventType());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid event type");
        }
        try {
            OperationType.valueOf(event.getOperation());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid operation");
        }

    }

    public EventDTO createFriendEvent(long userId, long friendId, OperationType operation) {
        EventDTO eventDto = new EventDTO();
        eventDto.setUserId(userId);
        eventDto.setEventType(EventType.FRIEND);
        eventDto.setOperation(operation);
        eventDto.setEntityId(friendId);
        return createEvent(eventDto);
    }

    public EventDTO createLikeEvent(long userId, long filmId, OperationType operation) {
        EventDTO eventDto = new EventDTO();
        eventDto.setUserId(userId);
        eventDto.setEventType(EventType.LIKE);
        eventDto.setOperation(operation);
        eventDto.setEntityId(filmId);
        return createEvent(eventDto);
    }

    public EventDTO createReviewEvent(long userId, long reviewId, OperationType operation) {
        EventDTO eventDto = new EventDTO();
        eventDto.setUserId(userId);
        eventDto.setEventType(EventType.REVIEW);
        eventDto.setOperation(operation);
        eventDto.setEntityId(reviewId);
        return createEvent(eventDto);
    }

    public EventDTO convertToDto(Event event) {
        long timestamp = event.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        return new EventDTO(
                timestamp,
                event.getUserId(),
                EventType.valueOf(event.getEventType()),
                OperationType.valueOf(event.getOperation()),
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
                .eventType(eventDto.getEventType().name())
                .operation(eventDto.getOperation().name())
                .entityId(eventDto.getEntityId())
                .createdAt(createdAt)
                .build();
    }
}
