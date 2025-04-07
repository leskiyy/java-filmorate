package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long eventId;

    private Long userId;

    private String eventType;

    private String operation;

    private Long entityId;

    private LocalDateTime createdAt;
}