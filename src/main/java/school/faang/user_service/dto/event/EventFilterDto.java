package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventFilterDto {
    private Long id;
    private String title;
    private String description;
    private EventType eventType;
    private Long ownerId;
    private List<Long> skillIds;
    private LocalDateTime startDateFrom;
    private LocalDateTime endDateFrom;
    private String location;
    private Integer maxAttendees;
    private Double minRating;
    private EventStatus status;
}
