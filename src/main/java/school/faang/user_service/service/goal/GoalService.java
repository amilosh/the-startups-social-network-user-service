package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDTO;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.List;
import java.util.stream.Stream;

@Component
@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    private final UserService userService;
    private final SkillService skillService;
    private final List<GoalFilter> goalFilters;

    private final GoalMapper goalMapper;

    private final GoalValidator goalValidation;


    /**
     * Creates a new goal with the given details for the given user ID.
     *
     * @param userId the ID of the user to create the goal for
     * @param goal the goal details
     * @return the newly created goal
     *
     * The validation of the goal request will fail if the request is invalid or
     * if the user does not exist. The goal will be created with the given details
     * and saved in the database.
     */
    public GoalDTO createGoal(Long userId, GoalDTO goal) {
        goalValidation.validateGoalRequest(userId, goal, true);

        goal.setId(null);
        goal.setStatus(GoalStatus.ACTIVE);

        Goal entity = goalMapper.toEntity(goal);
        entity.setUsers(List.of(userService.getUserById(userId)));

        if (goal.getParentGoalId() != null) {
            entity.setParent(goalRepository.findGoalById(goal.getParentGoalId()));
        }

        entity.setSkillsToAchieve(goal.getSkillIds().stream().map(skillService::getSkillById).toList());

        goalRepository.save(entity);

        return goalMapper.toDto(entity);
    }

    /**
     * Updates an existing goal with the given details for the given user ID.
     *
     * @param userId the ID of the user to update the goal for
     * @param goal the goal details
     * @return the updated goal
     *
     * The validation of the goal request will fail if the request is invalid or
     * if the user does not exist. The goal will be updated with the given details
     * and saved in the database.
     */
    public GoalDTO updateGoal(Long userId, GoalDTO goal) {
        goalValidation.validateGoalRequest(userId, goal, false);

        Goal entity = goalRepository.findGoalById(goal.getId());

        entity.setTitle(goal.getTitle());
        entity.setDescription(goal.getDescription());
        entity.setStatus(goal.getStatus());
        entity.setDeadline(goal.getDeadline());

        if (goal.getMentorId() != null) {
            entity.setMentor(userService.getUserById(goal.getMentorId()));
        }

        if (goal.getParentGoalId() != null) {
            entity.setParent(goalRepository.findGoalById(goal.getParentGoalId()));
        }

        goalRepository.save(entity);

        return goalMapper.toDto(entity);
    }

    /**
     * Deletes a goal by its ID.
     *
     * @param goalId the ID of the goal to be deleted
     *
     * The deletion will fail if the goal does not exist.
     */
    public void deleteGoal(long goalId) {
        goalRepository.delete(goalRepository.findGoalById(goalId));
    }

    /**
     * Retrieves a list of goals for a given user ID, filtered by the provided goal filter.
     *
     * @param userId the ID of the user whose goals are to be retrieved
     * @param filters the goal filter to apply on the retrieved goals
     * @return a list of goals for the given user ID, filtered by the provided goal filter
     */
    public List<GoalDTO> getGoalsByUser(long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId).stream();

        Stream<Goal> filteredGoals = goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(
                        goals,
                        (currentStream, filter) -> filter.apply(currentStream, filters),
                        (s1, s2) -> s1
                );

        return filteredGoals.map(goalMapper::toDto).toList();
    }
}
