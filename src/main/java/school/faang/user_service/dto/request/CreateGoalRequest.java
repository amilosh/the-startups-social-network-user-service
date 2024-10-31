package school.faang.user_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.GoalDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGoalRequest {
    private Long userId;
    private GoalDTO goal;
}
