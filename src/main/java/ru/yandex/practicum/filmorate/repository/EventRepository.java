package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class EventRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Event> getByUserId(long userId) {
        String sql = "SELECT * FROM events WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, this::mapRowToEvent, userId);
    }

    public List<Event> getAllEvents() {
        String sql = "SELECT * FROM events ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, this::mapRowToEvent);
    }

    public Event create(Event event) {
        String sql = "INSERT INTO events (user_id, event_type, operation, entity_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"event_id"});
            stmt.setLong(1, event.getUserId());
            stmt.setString(2, event.getEventType());
            stmt.setString(3, event.getOperation());
            stmt.setLong(4, event.getEntityId());
            stmt.setTimestamp(5, Timestamp.valueOf(event.getCreatedAt()));
            return stmt;
        }, keyHolder);

        event.setEventId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return event;
    }

    private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .entityId(rs.getLong("entity_id"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
