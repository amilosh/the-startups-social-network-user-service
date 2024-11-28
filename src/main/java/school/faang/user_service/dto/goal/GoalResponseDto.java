package school.faang.user_service.dto.goal;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

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