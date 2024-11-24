package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;
import lombok.Data;

import java.util.List;

@Data
@NoArgsConstructor
public class GoalResponseDto {

    private Long id;
    private Long parentId;
    private String title;
    private String description;
    private GoalStatus status;
    private List<Long> skillIds;
}