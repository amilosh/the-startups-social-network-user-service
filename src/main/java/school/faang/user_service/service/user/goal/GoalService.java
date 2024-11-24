package school.faang.user_service.service.user.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    public Stream<Goal> getGoalsByMentorId(long mentorId) {
        return goalRepository.findGoalsByMentorId(mentorId);
    }
}
