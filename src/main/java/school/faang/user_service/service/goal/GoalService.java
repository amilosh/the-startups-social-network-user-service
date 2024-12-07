package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.events.GoalCompletedEvent;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final UserContext userContext;
    private final GoalCompletedEventPublisher completedEventPublisher;
    private final GoalRepository goalRepository;
    private final GoalValidator goalValidator;

    public void completeGoal(long goalId){
        goalValidator.validateId(goalId);

        Goal goal = getGoal(goalId);
        goal.setStatus(GoalStatus.COMPLETED);
        saveGoal(goal);

        GoalCompletedEvent completedEvent = new GoalCompletedEvent();
        completedEvent.setUserId(userContext.getUserId());
        completedEvent.setGoalId(goalId);
        completedEvent.setGoalAchieveDateTime(LocalDateTime.now());

        completedEventPublisher.publish(completedEvent);
    }
    public Goal getGoal(long goalId){
        return goalRepository.findById(goalId)
                .orElseThrow(()->new IllegalArgumentException("Цель не найдена в базе данных"));
    }
    public void saveGoal(Goal goal){
        goalRepository.save(goal);
    }
}
