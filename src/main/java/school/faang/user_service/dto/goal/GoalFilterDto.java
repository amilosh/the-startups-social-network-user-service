package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
@NoArgsConstructor
public class GoalFilterDto {
    @Size(min = 1, max = 255, message = "The goal name pattern should be between 1 and 255 characters long")
    private String title;

    private GoalStatus status;
}