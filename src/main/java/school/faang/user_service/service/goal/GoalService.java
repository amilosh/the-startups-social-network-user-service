package school.faang.user_service.service.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        existingGoal.setStatus(GoalStatus.valueOf(String.valueOf(goalDto.getStatus())));

        if (GoalStatus.COMPLETED == GoalStatus.valueOf(String.valueOf(goalDto.getStatus()))) {
            List<User> users = goalRepository.findUsersByGoalId(goalId);
            for (User user : users) {
                for (Long skillId : goalDto.getSkillIds()) {
                    skillRepository.assignSkillToUser(skillId, user.getId());
                }
            }
        }

        return Optional.of(goalRepository.save(existingGoal));
    }

    public void deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new IllegalArgumentException("Goal not found.");
        }
        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, String statusFilter) {
        // Получаем поток подзадач по идентификатору родительской цели
        Stream<Goal> subtasksStream = goalRepository.findByParent(goalId);

        // Фильтруем подзадачи по статусу, если фильтр передан
        if (statusFilter != null && !statusFilter.isEmpty()) {
            subtasksStream = subtasksStream.filter(goal -> goal.getStatus().toString().equalsIgnoreCase(statusFilter));
        }

        // Преобразуем поток целей в список DTO
        return subtasksStream
                .map(this::convertToDto) // Преобразуем каждую цель в GoalDto
                .collect(Collectors.toList()); // Собираем результаты в список
    }

    private GoalDto convertToDto(Goal goal) {
        // Извлекаем ID у родительской цели, если она существует
        Long parentId = (goal.getParent() != null) ? goal.getParent().getId() : null;

        // Преобразуем список объектов Skill в список идентификаторов (skillIds)
        List<Long> skillIds = goal.getSkillsToAchieve().stream()
                .map(skill -> skill.getId())
                .collect(Collectors.toList());

        // Создаем и возвращаем DTO с заполненными значениями
        return new GoalDto(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                parentId, // Добавляем ID родительской цели, нужно ли делать фильтр по этим полям?
                goal.getStatus() != null ? goal.getStatus().toString() : null, // Преобразуем статус в строку
                skillIds // Добавляем список идентификаторов навыков
        );
    }

    public List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filter) {
        Stream<Goal> goalsStream = goalRepository.findGoalsByUserId(userId);

        // Применяем фильтры
        if (filter.getTitle() != null) {
            goalsStream = goalsStream.filter(goal -> goal.getTitle().contains(filter.getTitle()));
        }
        if (filter.getStatus() != null) {
            goalsStream = goalsStream.filter(goal -> goal.getStatus() == filter.getStatus());
        }
        if (filter.getSkillId() != null) {
            goalsStream = goalsStream.filter(goal -> goal.getSkillIds().contains(filter.getSkillId()));
        }

        // Преобразуем цель в DTO и возвращаем как список
        return goalsStream
                .map(goal -> new GoalDto(
                        goal.getId(),
                        goal.getDescription(),
                        goal.getParentId(),//нужно ли делать фильтр по этим полям?
                        goal.getTitle(),
                        goal.getStatus(),
                        goal.getSkillIds() //нужно ли делать фильтр по этим полям?
                ))
                .collect(Collectors.toList());
    }
}
