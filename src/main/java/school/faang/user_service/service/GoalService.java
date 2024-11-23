package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    @Transactional
    public Stream<Goal> getGoalsByMentorId(long mentorId) {
        return goalRepository.findGoalsByMentorId(mentorId);
    }

    public Goal saveGoal(Goal goal) {
        return goalRepository.save(goal);
    }
}
