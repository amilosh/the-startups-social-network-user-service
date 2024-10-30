package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {

    private Long id;

    @NotBlank
    @Size(max = 4096)
    private String description;
    private Long parentId;

    @NotBlank
    @Size(max = 64)
    private String title;
    private GoalStatus status;
    private List<Long> skillIds;
}
