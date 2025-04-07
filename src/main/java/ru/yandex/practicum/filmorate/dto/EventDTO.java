package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long timestamp;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long userId;

    private String eventType;

    private String operation;

    private Long eventId;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long entityId;
}
