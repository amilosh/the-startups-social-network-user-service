package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.validation.EnumeratedValid;

public record GoalFilterDto(
        @Size(max = 100, message = "The field 'about' can contain no more than 100 characters.")
        String title,
        @NotNull(message = "Status cannot be empty")
        @EnumeratedValid(enumClass = GoalStatus.class)
        GoalStatus status,
        @NotNull(message = "skill cannot be empty")
        Long skillId
) {
}