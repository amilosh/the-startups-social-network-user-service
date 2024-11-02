package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;
    private Long ownerId;
    private String title;
    private String description;
    private String location;
    private int maxAttendees;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private EventType type;
    private EventStatus status;
    private List<Long> relatedSkills;
}
