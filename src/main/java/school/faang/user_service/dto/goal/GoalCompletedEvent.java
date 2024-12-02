package school.faang.user_service.dto.goal;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoalCompletedEvent {
    private long goalId;
    private long userId;
    private LocalDateTime completedAt;
}
