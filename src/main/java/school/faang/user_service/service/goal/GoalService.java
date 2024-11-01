package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.MentorNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Value("${application.constants.max-active-goals-count}")
    private int maxActiveGoalsCount;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by Id: " + userId));

        int activeGoalsCount = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoalsCount >= maxActiveGoalsCount) {
            throw new IllegalStateException("User has reached the maximum number of active goals (" + maxActiveGoalsCount + ").");
        }
        Goal goalToSave = goalMapper.toGoal(goalDto);
        if (goalDto.getParentId() != null) {
            Goal parentGoal = goalRepository.findById(goalDto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Goal not found by Id: " + goalDto.getParentId()));
            goalToSave.setParent(parentGoal);
        }
        if (goalDto.getSkillIds() != null && !goalDto.getSkillIds().isEmpty()) {
            List<Skill> foundSkills = skillRepository.findAllById(goalDto.getSkillIds());

            if (foundSkills.size() != goalDto.getSkillIds().size()) {
                List<Long> missingSkillIds = new ArrayList<>(goalDto.getSkillIds());
                missingSkillIds.removeAll(foundSkills.stream().map(Skill::getId).toList());

                throw new EntityNotFoundException("Skills not found by IDs: " + missingSkillIds);
            }

            goalToSave.setSkillsToAchieve((List<Skill>) new HashSet<>(foundSkills));
        }
        if (goalDto.getMentorId() != null) {
            User mentor = userRepository.findById(goalDto.getMentorId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found by Id: " + goalDto.getMentorId()));
            if (!user.getMentors().contains(mentor)) {
                throw new MentorNotFoundException("Mentor with ID " + goalDto.getMentorId() +
                        " is not associated with user ID " + userId);
            }
            goalToSave.setMentor(mentor);
        }
        if (goalDto.getDeadline() != null) {
            goalToSave.setDeadline(goalDto.getDeadline());
        }
        goalToSave.setStatus(GoalStatus.ACTIVE);
        Goal savedGoal = goalRepository.save(goalToSave);
        return goalMapper.toGoalDto(savedGoal);
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found by Id: " + goalId));

        if (goalDto.getStatus() == GoalStatus.COMPLETED && goalToUpdate.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalStateException("Goal is already completed and cannot be updated further.");
        }
        if (goalDto.getSkillIds() != null && !goalDto.getSkillIds().isEmpty()) {
            List<Skill> foundSkills = skillRepository.findAllById(goalDto.getSkillIds());
            if (foundSkills.size() != goalDto.getSkillIds().size()) {
                List<Long> missingSkillIds = new ArrayList<>(goalDto.getSkillIds());
                missingSkillIds.removeAll(foundSkills.stream().map(Skill::getId).toList());

                throw new EntityNotFoundException("Skills not found by IDs: " + missingSkillIds);
            }

            goalToUpdate.setSkillsToAchieve((List<Skill>) new HashSet<>(foundSkills));
        }

        goalToUpdate.setTitle(goalDto.getTitle());
        goalToUpdate.setDescription(goalDto.getDescription());
        goalToUpdate.setStatus(goalDto.getStatus());
        goalToUpdate.setDeadline(goalDto.getDeadline());

        Goal savedGoal = goalRepository.save(goalToUpdate);
        return goalMapper.toGoalDto(savedGoal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        Goal goalToDelete = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found by Id: " + goalId));
        if (goalToDelete.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalStateException("Cannot delete a completed goal. Please unmark it before deletion.");
        }
        goalRepository.delete(goalToDelete);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);

        return filterGoals(goals, filterDto);
    }

    @Transactional
    public List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.getGoalsByUserIdId(userId);

        return filterGoals(goals, filterDto);
    }

    private List<GoalDto> filterGoals(Stream<Goal> goals, GoalFilterDto filterDto) {
        return goalFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(goals, (currentGoals, filter) -> filter.apply(currentGoals, filterDto), (s1, s2) -> s1)
                .map(goalMapper::toGoalDto)
                .toList();
    }

}
