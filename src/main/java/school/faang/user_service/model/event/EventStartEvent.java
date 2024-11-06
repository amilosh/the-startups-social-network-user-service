package school.faang.user_service.model.event;

import school.faang.user_service.model.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStartEvent {
    private Long eventId;
    private List<Long> participantIds;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventStartEvent that = (EventStartEvent) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(participantIds, that.participantIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, participantIds);
    }
}
