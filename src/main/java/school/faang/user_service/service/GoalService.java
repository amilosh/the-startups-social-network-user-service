package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    public void removeGoalsWithoutUsers(List<Goal> goals) {
        goals.stream()
                .filter(Goal::isEmptyUsers)
                .forEach(goal -> goalRepository.deleteById(goal.getId()));
    }

    public List<Goal> mapListIdsToGoals(List<Long> goalsIds) {
        return goalsIds.stream()
                .map(id -> getGoalById(id))
                .toList();
    }

    public Goal getGoalById(long id) {
        return goalRepository.findById(id).orElseThrow(() -> new DataValidationException("Goal do not found"));
    }
}