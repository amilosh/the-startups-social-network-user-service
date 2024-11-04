package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {
    private static final int MAX_NUM_ACTIVE_GOALS = 3;

    private final GoalRepository goalRepo;
    private final GoalMapper goalMapper;
    private final UserService userService;
    private final SkillService skillService;

    @Transactional
    public GoalResponseDto create(CreateGoalDto createGoalDto) {
        Goal transientGoal = mapToFullGoal(createGoalDto);
        validateGoal(transientGoal);

        transientGoal.getUsers().forEach(user -> user.addGoal(transientGoal));
        transientGoal.getSkillsToAchieve().forEach(skill -> skill.addGoal(transientGoal));
        Goal persistantGoal = goalRepo.save(transientGoal);
        return goalMapper.toResponseDto(persistantGoal);
    }

    @Transactional
    public void delete(long goalId) {
        deleteGoalWithChildren(goalId);
    }

    private void deleteGoalWithChildren(Long goalId) {
        Goal persistantGoal = goalRepo.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", goalId));

        List<Goal> children = goalRepo.findAllByParentId(goalId);
        for (Goal child : children) {
            deleteGoalWithChildren(child.getId());
        }
        persistantGoal.getSkillsToAchieve().forEach(skill -> skill.removeGoal(persistantGoal));
        persistantGoal.getUsers().forEach(user -> user.removeGoal(persistantGoal));
        goalRepo.delete(persistantGoal);

        log.info("Successfully deleted goal with id {} and all its children", goalId);
    }

    private void validateGoal(Goal goal) {
        boolean isAnyUserHasMaxNumOfGoals = goal.getUsers().stream()
                .anyMatch(user -> user.hasMaxNumOfGoals(MAX_NUM_ACTIVE_GOALS));
        if (isAnyUserHasMaxNumOfGoals) {
            throw new DataValidationException("The user has the maximum number of goals");
        }
    }

    @NotNull
    private Goal mapToFullGoal(CreateGoalDto createGoalDto) {
        if (createGoalDto == null) {
            throw new IllegalArgumentException("recommendationDto must not be null");
        }

        Goal goal = goalMapper.toEntity(createGoalDto);
        if (createGoalDto.parentId() != null) {
            Goal parentGoal = goalRepo.findById(createGoalDto.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", createGoalDto.parentId()));
            goal.setParent(parentGoal);
        }

        if (createGoalDto.mentorId() != null) {
            User user = userService.getUserById(createGoalDto.mentorId());
            goal.setMentor(user);
        }

        List<User> users = userService.getAllUsersByIds(createGoalDto.usersId());
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", createGoalDto.usersId());
        }
        goal.setUsers(users);

        List<Skill> skills = skillService.getAllSkillsByIds(createGoalDto.skillsToAchieveIds());
        if (skills.isEmpty()) {
            throw new ResourceNotFoundException("Skill", "id", createGoalDto.skillsToAchieveIds());
        }
        goal.setSkillsToAchieve(skills);
        goal.setStatus(GoalStatus.ACTIVE);

        return goal;
    }
}
