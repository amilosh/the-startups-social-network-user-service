package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GoalFilterDto(
        String title,
        String description,
        GoalStatus status,
        LocalDateTime deadline,
        List<Long> skillIds,
        List<Long> userIds
) {
}
