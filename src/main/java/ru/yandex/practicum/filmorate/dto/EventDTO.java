package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.utils.EventType;
import ru.yandex.practicum.filmorate.utils.OperationType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long timestamp;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long userId;

    private EventType eventType;

    private OperationType operation;

    private Long eventId;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long entityId;
}
