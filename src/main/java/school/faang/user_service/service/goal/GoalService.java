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
        // Находим цель по ID
        Optional<Goal> existingGoalOptional = goalRepository.findById(goalId);

        if (existingGoalOptional.isPresent()) {
            Goal existingGoal = existingGoalOptional.get();

            // Проверка: цель должна иметь название
            if (goalDto.getTitle() == null || goalDto.getTitle().isEmpty()) {
                return Optional.empty(); // Название должно быть
            }

            // Проверка: нельзя обновлять завершенную цель
            if (existingGoal.getStatus() == GoalStatus.COMPLETED) {
                return Optional.empty(); // Завершенные цели нельзя обновлять
            }

            // Проверка существования скиллов
            if (skillRepository.countExisting(goalDto.getSkillIds()) != goalDto.getSkillIds().size()) {
                return Optional.empty(); // Скиллы должны существовать
            }

            // Обновляем цель
            existingGoal.setTitle(goalDto.getTitle());
            existingGoal.setDescription(goalDto.getDescription());
            existingGoal.setParent(goalDto.getParentId());

            // Проверяем статус цели
            if (goalDto.getStatus() == GoalStatus.COMPLETED) {
                // Присваиваем скиллы всем участникам
                List<User> users = goalRepository.findUsersByGoalId(goalId);
                for (User user : users) {
                    for (Long skillId : goalDto.getSkillIds()) {
                        skillRepository.assignSkillToUser(skillId, user.getId());
                    }
                }
            }

            // Сохраняем обновленную цель в базе
            return Optional.of(goalRepository.save(existingGoal));
        }

        return Optional.empty(); // Цель с таким ID не найдена
    }
}
