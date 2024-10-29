package school.faang.user_service.service.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;
import school.faang.user_service.entity.goal.Goal;

@Service
public class GoalService {

    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;

    @Autowired
    public GoalService(GoalRepository goalRepository, SkillRepository skillRepository) {
        this.goalRepository = goalRepository;
        this.skillRepository = skillRepository;
    }

    public Optional<Goal> createGoal(Long userId, Goal goal, List<Long> skillIds) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_ACTIVE_GOALS) {
            return Optional.empty();
        }

        if (skillRepository.countExisting(skillIds) != skillIds.size()) {
            return Optional.empty();
        }

        Goal createdGoal = goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
        return Optional.of(createdGoal);
    }
}
