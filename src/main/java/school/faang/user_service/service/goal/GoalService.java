package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.Data;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.List;

@RequiredArgsConstructor
@Data
@Component
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalValidator goalValidator;
    private final GoalMapper goalMapper;
    private final SkillRepository skillRepository;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateCreationGoal(userId, goalDto);

        Goal saveGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getId());

        goalDto.getSkillIds().forEach(skillId -> goalRepository.addSkillToGoal(saveGoal.getId(), skillId));
        return goalMapper.toDto(saveGoal);
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal existingGoal = goalRepository.findById(goalDto.getId())
                .orElseThrow(() -> new DataValidationException("Goal not found"));

        goalValidator.validateUpdatingGoal(goalId, goalDto);

        assignSkillsToUsers(goalDto.getSkillIds(), existingGoal.getId());

        updateGoalSkills(existingGoal.getId(), goalDto.getSkillIds());
        Goal updatedGoal = goalMapper.toEntity(goalDto);

        goalRepository.save(updatedGoal);

        return goalMapper.toDto(updatedGoal);
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    private void assignSkillsToUsers(List<Long> skillIds, Long goalId) {
        List<User> users = goalRepository.findUsersByGoalId(goalId);
        for (User user : users) {
            for (Long skillId : skillIds) {
                skillRepository.assignSkillToUser(skillId, user.getId());
            }
        }
    }

    private void updateGoalSkills(Long goalId, List<Long> skillIds) {
        goalRepository.removeSkillsFromGoal(goalId);
        skillIds.forEach(skillId -> goalRepository.addSkillToGoal(skillId, goalId));
    }
}