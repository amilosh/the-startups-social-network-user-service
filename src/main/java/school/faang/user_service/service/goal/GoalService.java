package school.faang.user_service.service.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        List<Skill> skills = new ArrayList<>();
        validateSkills(goalDto.getSkillsIds(), userId);

        Goal savedGoal = new Goal();
        savedGoal.setTitle(goalDto.getTitle());
        savedGoal.setDescription(goalDto.getDescription());
        savedGoal.setId(goalDto.getParentId());
        savedGoal.setSkillsToAchieve(new ArrayList<>());
        savedGoal = goalRepository.save(savedGoal);
        savedGoal.getSkillsToAchieve().addAll(skills);

        return new GoalDto(savedGoal);
    }

    private void validateSkills(List<Long> skillIds, Long userId) {
        int activeGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoals > MAX_ACTIVE_GOALS) {
            throw new IllegalArgumentException("User has reached the maximum goals");
        }
        for (Long skillId : skillIds) {
            if (!skillRepository.existsById(skillId)) {
                throw new IllegalArgumentException("Skill with ID " + skillId + " doesn't exist");
            }
        }
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        List<Long> skillsIds = goalDto.getSkillsIds();
        Goal existingGoal = goalRepository.findById(goalId).orElseThrow(() -> new IllegalArgumentException("Goal not found"));
        if (existingGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot update a completed goal");
        }

        validateSkills(skillsIds, goalId);
        existingGoal.setTitle(goalDto.getTitle());
        existingGoal.setDescription(goalDto.getDescription());
        existingGoal.setStatus(goalDto.getStatus());

        if (existingGoal.getSkillsToAchieve() == null) {
            existingGoal.setSkillsToAchieve(new ArrayList<>());
        }

        List<Skill> skills = new ArrayList<>();
        for (Long skillId : skillsIds) {
            List<Skill> foundSkills = skillRepository.findAllByUserId(skillId);
            if (foundSkills.isEmpty()) {
                throw new IllegalArgumentException("Skill with ID " + skillId + " doesn't exist");
            }

            skills.add(foundSkills.get(0));
        }

        existingGoal.getSkillsToAchieve().clear();
        existingGoal.getSkillsToAchieve().addAll(skills);
        goalRepository.save(existingGoal);

        return new GoalDto(existingGoal);

    }

    public void deleteGoal(long goalId) {

        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new IllegalArgumentException(("Goal not found")));

        if (goal.getSkillsToAchieve() == null) {
            goal.setSkillsToAchieve(new ArrayList<>());
        }

        goal.getSkillsToAchieve().clear();
        goalRepository.save(goal);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {

        return goalRepository.findByParent(goalId).filter(filter::matches).map(GoalDto::new).collect(Collectors.toList());
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        return goalRepository.findGoalsByUserId(userId).filter(filter::matches).map(GoalDto::new).collect(Collectors.toList());
    }
}




