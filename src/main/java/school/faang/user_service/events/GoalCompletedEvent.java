package school.faang.user_service.events;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoalCompletedEvent {
    private long userId;
    private long goalId;
    private LocalDateTime goalAchieveDateTime;
}
