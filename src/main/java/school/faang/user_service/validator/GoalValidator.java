package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    public static final int MAX_USER_GOALS_SIZE = 3;

    private final GoalRepository goalRepository;

    public void validateCreate(GoalDto goalDto, long userId, Optional<User> user, Optional<Goal> parentGoal, List<Skill> skills) {
        validateUser(userId, user);

        validateTitle(goalDto);
        validateDescription(goalDto);
        validateParent(goalDto.getParentId(), parentGoal);
        validateSkills(goalDto.getSkillToAchieveIds(), skills);
    }

    public void validateUpdate(long goalId, Optional<Goal> goal, GoalDto goalDto, Optional<Goal> parentGoal, List<Skill> skills) {
        validateGoalOnUpdate(goalId, goal);

        validateTitle(goalDto);
        validateDescription(goalDto);
        validateParent(goalDto.getParentId(), parentGoal);
        validateSkills(goalDto.getSkillToAchieveIds(), skills);
    }

    public void validateUser(long userId, Optional<User> userOpt) {
        User user = userOpt
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " doesn't exist"));
        if (user.getGoals().size() > MAX_USER_GOALS_SIZE - 1) {
            throw new IllegalArgumentException("Number of goals should be no more than " + MAX_USER_GOALS_SIZE);
        }
    }

    public void validateGoalOnUpdate(long goalId, Optional<Goal> goalOpt) {
        Goal goal = goalOpt.orElseThrow(
                () -> new IllegalArgumentException("Goal with id " + goalId + " doesn't exist")
        );

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("Goal with id " + goalId + " is already completed. Completed goals cannot be changed.");
        }
    }

    public void validateTitle(GoalDto goalDto) {
        boolean isTitleNotUnique = goalRepository.existsGoalByTitle(goalDto.getTitle());
        if (isTitleNotUnique) {
            throw new IllegalArgumentException("Goal with title \"%s\" already exist".formatted(goalDto.getTitle()));
        }
    }

    public void validateDescription(GoalDto goalDto) {
        boolean isDescriptionNotUnique = goalRepository.existsGoalByDescription(goalDto.getDescription());
        if (isDescriptionNotUnique) {
            throw new IllegalArgumentException("Goal with description \"%s\" already exist".formatted(goalDto.getDescription()));
        }
    }

    public void validateParent(long parentGoalId, Optional<Goal> parentGoal) {
        parentGoal.orElseThrow(
                () -> new IllegalArgumentException("Parent goal with id " + parentGoalId + " doesn't exist")
        );
    }

    public void validateSkills(List<Long> skillIds, List<Skill> skills) {
        checkSkillsExistingByIds(skillIds, skills);
    }

    public void checkSkillsExistingByIds(List<Long> skillToAchieveIds, List<Skill> skills) {
        Set<Long> ids = skills.stream().map(Skill::getId).collect(Collectors.toSet());
        String notValidSkillIds = skillToAchieveIds.stream()
                .filter(id -> !ids.contains(id))
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        if (!notValidSkillIds.isEmpty()) {
            throw new IllegalArgumentException("Skills with this ids don't exist: " + notValidSkillIds);
        }
    }
}
