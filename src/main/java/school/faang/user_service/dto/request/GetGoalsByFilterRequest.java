package school.faang.user_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.GoalFilterDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetGoalsByFilterRequest {
    private GoalFilterDto filters;
}
