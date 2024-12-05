package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final UserService userService;
    private final SkillService skillService;

    public GoalDto create(long userId, GoalDto goalDto) {
        Optional<User> user = userService.findById(userId);
        Optional<Goal> parentGoalOpt = Optional.ofNullable(goalDto.getParentId())
                .flatMap(goalRepository::findById);
        List<Skill> skills = Optional.ofNullable(goalDto.getSkillToAchieveIds())
                .map(skillService::findByIdIn)
                .orElse(new ArrayList<>());

        goalValidator.validateCreate(goalDto, userId, user, parentGoalOpt, skills);

        Goal goal = goalMapper.toEntity(goalDto);

        goal.setUsers(List.of(user.get()));
        goal.setParent(parentGoalOpt.orElse(null));
        goal.setSkillsToAchieve(skills);
        goal.setStatus(GoalStatus.ACTIVE);

        return goalMapper.toDto(goalRepository.save(goal));
    }

    public GoalDto update(long goalId, GoalDto goalDto) {
        Optional<Goal> goalOpt = goalRepository.findById(goalId);
        Optional<Goal> parentGoalOpt = Optional.ofNullable(goalDto.getParentId())
                .flatMap(goalRepository::findById);
        List<Skill> skills = Optional.ofNullable(goalDto.getSkillToAchieveIds())
                .map(skillService::findByIdIn)
                .orElse(new ArrayList<>());

        goalValidator.validateUpdate(goalId, goalOpt, goalDto, parentGoalOpt, skills);

        Goal goal = goalOpt.get();
        GoalStatus pastStatus = goal.getStatus();
        goalMapper.updateGoal(goal, goalDto);

        goal.setParent(parentGoalOpt.orElse(null));
        goal.setSkillsToAchieve(skills);

        if (goalMapper.toGoalStatus(goalDto.getStatus()) == GoalStatus.COMPLETED && pastStatus == GoalStatus.ACTIVE) {
            goal.getUsers().forEach(user -> {
                addNewSkills(user, goal);
            });
        }
        return goalMapper.toDto(goalRepository.save(goal));
    }

    public void delete(long goalId) {
        goalRepository.deleteById(goalId);
    }

    private void addNewSkills(User user, Goal goal) {
        List<Skill> userSkills = user.getSkills();
        Set<Long> addedSkillIds = userSkills.stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());
        List<Skill> skillsToAdd = goal.getSkillsToAchieve()
                .stream()
                .filter(skill -> !addedSkillIds.contains(skill.getId()))
                .toList();

        userSkills.addAll(skillsToAdd);
    }
}
