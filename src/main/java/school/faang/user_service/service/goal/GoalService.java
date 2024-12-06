package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalCompletedEvent;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.publisher.goal.GoalCompletedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;
import school.faang.user_service.validator.goal.GoalValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalValidator goalValidator;
    private final GoalMapper goalMapper;
    private final SkillRepository skillRepository;
    private final GoalCompletedEventPublisher goalCompletedPublisher;
    private final UserContext userContext;
    private final List<GoalFilter> goalFilters;

    @Transactional
    public GoalResponseDto createGoal(Long userId, GoalRequestDto goalDto) {
        goalValidator.validateCreationGoal(userId, goalDto);
        log.debug("Validation successful for goal creation: {}", goalDto);

        Goal saveGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        log.info("Goal created with ID: {}", saveGoal.getId());

        goalDto.getSkillIds().forEach(skillId -> {
            goalRepository.addSkillToGoal(saveGoal.getId(), skillId);
            log.debug("Skill with ID: {} assigned to goal with ID: {}", skillId, saveGoal.getId());
        });

        return goalMapper.toDto(saveGoal);
    }

    @Transactional
    public GoalResponseDto updateGoal(Long goalId, GoalRequestDto goalDto) {
        Goal existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new DataValidationException("Goal not found"));
        log.info("Existing goal found: {}", existingGoal);

        goalValidator.validateUpdatingGoal(goalId, goalDto);

        assignSkillsToUsers(goalDto.getSkillIds(), existingGoal.getId());

        updateGoalSkills(existingGoal.getId(), goalDto.getSkillIds());

        Goal updatedGoal = goalMapper.toEntity(goalDto);
        updatedGoal.setId(goalId);

        goalRepository.save(updatedGoal);
        log.info("Goal updated successfully: {}", updatedGoal);
        if (goalDto.getStatus() == GoalStatus.COMPLETED) {
            long authorId = userContext.getUserId();
            goalCompletedPublisher.publish(new GoalCompletedEvent(goalId, authorId, LocalDateTime.now()));
            log.info("Completed goal {} successfully submitted to redis ", goalId);
        }

        return goalMapper.toDto(updatedGoal);
    }

    @Transactional
    public void deleteGoal(Long goalId) {

        goalRepository.deleteById(goalId);
        log.info("Goal deleted successfully with ID: {}", goalId);
    }

    public List<GoalResponseDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filters) {
        Stream<Goal> subtasks = goalRepository.findByParent(goalId);

        List<GoalResponseDto> result = goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(subtasks, filters))
                .map(goalMapper::toDto)
                .toList();

        log.info("Found {} subtasks for goalId: {}", result.size(), goalId);
        return result;
    }

    public List<GoalResponseDto> getGoalsByUser(Long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);

        List<GoalResponseDto> result = goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(goals, filters))
                .map(goalMapper::toDto)
                .toList();

        log.info("Retrieved {} goals for userId: {}", result.size(), userId);
        return result;
    }

    private void assignSkillsToUsers(List<Long> skillIds, Long goalId) {
        List<User> users = goalRepository.findUsersByGoalId(goalId);
        for (User user : users) {
            for (Long skillId : skillIds) {
                skillRepository.assignSkillToUser(skillId, user.getId());
                log.debug("Skill with ID: {} assigned to user with ID: {}", skillId, user.getId());
            }
        }
    }

    private void updateGoalSkills(Long goalId, List<Long> skillIds) {
        goalRepository.removeSkillsFromGoal(goalId);
        skillIds.forEach(skillId -> goalRepository.addSkillToGoal(skillId, goalId));
        log.info("Skills updated successfully for goalId: {}", goalId);
    }
}