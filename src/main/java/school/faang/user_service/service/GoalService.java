package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.specification.GoalSpecification;
import school.faang.user_service.util.CollectionUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {
    private static final int MAX_NUM_ACTIVE_GOALS = 3;
    private static final String GOAL_COMPLETED_ERROR = "Goal is already completed";
    public static final String MAXIMUM_NUMBER_OF_GOALS_ERROR = "The user has the maximum number of goals";

    private final GoalRepository goalRepo;
    private final GoalMapper goalMapper;
    private final UserService userService;
    private final SkillService skillService;

    @Transactional
    public GoalResponseDto create(CreateGoalDto createGoalDto) {
        Goal transientGoal = createInitialGoal(createGoalDto);
        establishAllRelations(transientGoal, createGoalDto);
        validateMaxActiveGoalsLimit(transientGoal);

        Goal persistantGoal = goalRepo.save(transientGoal);
        return goalMapper.toResponseDto(persistantGoal);
    }

    private Goal createInitialGoal(CreateGoalDto dto) {
        Goal goal = goalMapper.toEntity(dto);
        goal.setStatus(GoalStatus.ACTIVE);
        return goal;
    }

    private void establishAllRelations(Goal goal, CreateGoalDto dto) {
        setParentGoalIfProvided(goal, dto.parentId());
        setMentorIfProvided(goal, dto.mentorId());

        List<User> users = userService.getAllUsersByIds(dto.usersId());
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", dto.usersId());
        }
        goal.setUsers(users);
        users.forEach(user -> user.addGoal(goal));

        List<Skill> skills = skillService.getAllSkillsByIds(dto.skillsToAchieveIds());
        if (skills.isEmpty()) {
            throw new ResourceNotFoundException("Skill", "id", dto.skillsToAchieveIds());
        }
        goal.setSkillsToAchieve(skills);
        skills.forEach(skill -> skill.addGoal(goal));
    }

    @Transactional
    public void delete(long goalId) {
        deleteGoalWithChildren(goalId);
    }

    private void deleteGoalWithChildren(Long goalId) {
        Deque<Goal> goalsToDelete = new ArrayDeque<>();
        Goal rootGoal = goalRepo.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", goalId));
        goalsToDelete.push(rootGoal);

        while (!goalsToDelete.isEmpty()) {
            Goal currentGoal = goalsToDelete.peek();
            List<Goal> children = goalRepo.findAllByParentId(currentGoal.getId());

            if (children.isEmpty()) {
                Goal goalToDelete = goalsToDelete.pop();
                removeGoalReferences(goalToDelete);
                goalRepo.delete(goalToDelete);
            } else {
                goalsToDelete.addAll(children);
            }
        }

        log.info("Successfully deleted goal with id {} and all its children", goalId);
    }

    private void removeGoalReferences(Goal goal) {
        goal.getSkillsToAchieve().forEach(skill -> skill.removeGoal(goal));
        goal.getUsers().forEach(user -> user.removeGoal(goal));
    }

    @Transactional
    public GoalResponseDto update(Long goalId, UpdateGoalDto updateGoalDto) {
        Goal persistanceGoal = goalRepo.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", goalId));
        validateGoalNotCompleted(persistanceGoal);

        updateBasicProperties(persistanceGoal, updateGoalDto);
        updateRelations(persistanceGoal, updateGoalDto);

        validateMaxActiveGoalsLimit(persistanceGoal);
        persistanceGoal = goalRepo.save(persistanceGoal);
        return goalMapper.toResponseDto(persistanceGoal);
    }

    private void updateBasicProperties(Goal goal, UpdateGoalDto dto) {
        goal.setStatus(GoalStatus.valueOf(dto.status().name()));
        goal.setTitle(dto.title());
        goal.setDescription(dto.description());
        goal.setDeadline(dto.deadline());
    }

    private void updateRelations(Goal goal, UpdateGoalDto dto) {
        setParentGoalIfProvided(goal, dto.parentId());
        setMentorIfProvided(goal, dto.mentorId());
        updateUsersInGoal(goal, dto);
        updateSkillsToAchieveInGoal(goal, dto);
    }

    private void updateSkillsToAchieveInGoal(Goal persistanceGoal, UpdateGoalDto updateGoalDto) {
        List<Skill> updatedSkills = skillService.getAllSkillsByIds(updateGoalDto.skillsToAchieveIds());
        if (updatedSkills.isEmpty()) {
            throw new ResourceNotFoundException("Skill", "id", updateGoalDto.skillsToAchieveIds());
        }
        List<Skill> oldSkills = persistanceGoal.getSkillsToAchieve();
        List<Skill> removedSkills = CollectionUtils.findMissingElements(oldSkills, updatedSkills);
        List<Skill> newSkills = CollectionUtils.findMissingElements(updatedSkills, oldSkills);

        removedSkills.forEach(skill -> skill.removeGoal(persistanceGoal));
        newSkills.forEach(skill -> skill.addGoal(persistanceGoal));
        persistanceGoal.updateSkills(updatedSkills);
    }

    private void updateUsersInGoal(Goal persistanceGoal, UpdateGoalDto updateGoalDto) {
        List<User> updatedUsers = userService.getAllUsersByIds(updateGoalDto.userIds());
        if (updatedUsers.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", updateGoalDto.userIds());
        }
        List<User> oldUsers = persistanceGoal.getUsers();
        List<User> removedUsers = CollectionUtils.findMissingElements(oldUsers, updatedUsers);
        List<User> newUsers = CollectionUtils.findMissingElements(updatedUsers, oldUsers);

        removedUsers.forEach(user -> user.removeGoal(persistanceGoal));
        newUsers.forEach(user -> user.addGoal(persistanceGoal));
        persistanceGoal.updateUsers(updatedUsers);
    }

    private void setParentGoalIfProvided(Goal goal, Long parentId) {
        if (parentId != null) {
            Goal parentGoal = goalRepo.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", parentId));
            goal.setParent(parentGoal);
        }
    }

    private void setMentorIfProvided(Goal goal, Long mentorId) {
        if (mentorId != null) {
            User user = userService.getUserById(mentorId);
            goal.setMentor(user);
        }
    }

    public Page<GoalResponseDto> findSubtasksByGoalId(Long goalId, Pageable pageable) {
        Page<Goal> goals = goalRepo.findAllByParentId(goalId, pageable);
        return goals.map(goalMapper::toResponseDto);
    }

    public Page<GoalResponseDto> findGoalsByFilters(GoalFilterDto filters, Pageable pageable) {
        Page<Goal> goal = goalRepo.findAll(GoalSpecification.build(filters), pageable);
        return goal.map(goalMapper::toResponseDto);
    }

    private void validateMaxActiveGoalsLimit(Goal goal) {
        boolean isAnyUserHasMaxNumOfGoals = goal.getUsers().stream()
                .anyMatch(user -> user.hasMaxNumOfGoals(MAX_NUM_ACTIVE_GOALS));
        if (isAnyUserHasMaxNumOfGoals) {
            throw new DataValidationException(MAXIMUM_NUMBER_OF_GOALS_ERROR);
        }
    }

    private void validateGoalNotCompleted(Goal goal) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new DataValidationException(GOAL_COMPLETED_ERROR);
        }
    }
}
