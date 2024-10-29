package school.faang.user_service.service.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalStatus;
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

    public Optional<Goal> updateGoal(Long goalId, GoalDto goalDto) {
        Goal existingGoal = goalRepository.findById(goalId).orElseThrow(() ->
                new IllegalArgumentException("Goal not found."));

        if (existingGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update a completed goal.");
        }

        if (goalDto.getTitle() == null || goalDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Goal title is required.");
        }

        for (Long skillId : goalDto.getSkillIds()) {
            if (!skillRepository.existsById(skillId)) {
                throw new IllegalArgumentException("Skill with ID " + skillId + " does not exist.");
            }
        }

        existingGoal.setTitle(goalDto.getTitle());
        existingGoal.setDescription(goalDto.getDescription());
        existingGoal.setStatus(GoalStatus.valueOf(goalDto.getStatus()));

        if (GoalStatus.COMPLETED == GoalStatus.valueOf(goalDto.getStatus())) {
            List<User> users = goalRepository.findUsersByGoalId(goalId);
            for (User user : users) {
                for (Long skillId : goalDto.getSkillIds()) {
                    skillRepository.assignSkillToUser(skillId, user.getId());
                }
            }
        }

        return Optional.of(goalRepository.save(existingGoal));
    }
}
