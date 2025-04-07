package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long eventId;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long userId;

    private String eventType;

    private String operation;

    private Long entityId;

    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    public long getTimestamp() {
        return createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}