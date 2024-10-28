package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.*;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.validation.EnumeratedValid;

import java.time.LocalDateTime;
import java.util.List;

public record GoalDto(
        @NotNull(message = "Id cannot be empty")
        Long id,
        @NotNull(message = "ParentId cannot be empty")
        Long parentId,
        @NotEmpty(message = "Title cannot be empty")
        @Size(max = 100, message = "Title can contain no more than 100 characters.")
        String title,
        @Size(max = 200, message = "Description can contain no more than 200 characters.")
        String description,
        @NotNull(message = "Status cannot be empty")
        @EnumeratedValid(enumClass = GoalStatus.class)
        GoalStatus status,
        @FutureOrPresent(message = "Deadline date must be in the future or present tense.")
        LocalDateTime deadline,
        @PastOrPresent(message = "The creation date must be in the past or present tense.")
        LocalDateTime createdAt,
        @PastOrPresent(message = "Update date must be in the past or present tense.")
        LocalDateTime updatedAt,
        @NotNull(message = "Mentor cannot be empty.")
        Long mentorId,
        @NotNull(message = "Skill cannot be empty.")
        @NotEmpty(message = "Skill cannot be empty.")
        List<Long> skillIds,
        @NotNull(message = "List of users cannot be empty.")
        @NotEmpty(message = "List of users cannot be empty.")
        List<Long> userIds
) {
}