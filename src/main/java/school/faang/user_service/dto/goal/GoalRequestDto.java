package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@NoArgsConstructor
public class GoalRequestDto {

    @NotNull(message = "The parent's ID should not be empty")
    private Long parentId;

    @NotEmpty(message = "The goal name should not be empty")
    @NotBlank(message = "The goal name should not be blank")
    @Size(min = 1, max = 64, message = "The event name should be between 1 and 64 characters long")
    private String title;

    @NotEmpty(message = "The description of the goal should not be empty")
    @NotBlank(message = "The description of the goal should not be blank")
    @Size(min = 1, max = 4096, message = "The description of the goal should be between 1 and 4096 characters long")
    private String description;

    private GoalStatus status;

    @NotNull(message = "Skillids can't be null")
    private List<Long> skillIds;
}