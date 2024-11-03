package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.Data;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.goal.GoalValidator;

@RequiredArgsConstructor
@Data
@Component
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalValidator goalValidator;
    private final GoalMapper goalMapper;


    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateGoal(userId, goalDto);

        Goal saveGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getId());

        goalDto.getSkillIds().forEach(skillId -> goalRepository.addSkillToGoal(saveGoal.getId(), skillId));
        return goalMapper.toDto(saveGoal);
    }
}