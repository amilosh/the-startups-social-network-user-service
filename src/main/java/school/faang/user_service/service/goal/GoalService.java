package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalCompletedEvent;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalCompletedEventPublisher goalCompletedEventPublisher;
    private final GoalRepository goalRepository;

    public void completeGoal(Long completingUserId, Long goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow();
        goal.setStatus(GoalStatus.COMPLETED);
        goalRepository.save(goal);
        //создал это метод для отдельной задачи
        goalCompletedEventPublisher.publish(new GoalCompletedEvent(goalId, completingUserId, LocalDateTime.now()));
    }
}
