package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GoalCompletedEvent {
    private Long id;
    private Long completingUserId;
    private LocalDateTime date;
}
