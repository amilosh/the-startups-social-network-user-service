package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;

import java.util.List;

public interface GoalService {

    GoalDto createGoal(Long userId, GoalDto goalDto);

    GoalDto updateGoal(Long goalId, GoalDto goalDto);

    void deleteGoal(Long goalId);

    List<GoalDto> getGoalsByUserId(Long userId, GoalFilterDto filters);

    List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filters);
}
