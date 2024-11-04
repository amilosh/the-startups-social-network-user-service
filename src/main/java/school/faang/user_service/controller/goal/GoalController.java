package school.faang.user_service.controller.goal;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Data
@RequiredArgsConstructor
@Component
public class GoalController {

    private final GoalService goalService;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        validateGoalTitle(goalDto);
        return goalService.createGoal(userId, goalDto);
    }

    public GoalDto updateGoal(Long userId, GoalDto goalDto) {
        validateGoalTitle(goalDto);
        return goalService.updateGoal(userId, goalDto);
    }

    public void deleteGoal(Long goalId) {
        goalService.deleteGoal(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filters){
        return goalService.findSubtasksByGoalId(goalId, filters);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters){
        return goalService.getGoalsByUser(userId, filters);
    }

    private void validateGoalTitle(GoalDto goalDto) {
        if (goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            throw new DataValidationException("Title can't be empty");
        }
    }
}