package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    public Goal findGoalById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal with id: %s not found".formatted(id)));
    }

    public Stream<Goal> findGoalsByUserId(long userId) {
        return goalRepository.findGoalsByUserId(userId);
    }

    public int countActiveGoalsPerUser(long userId) {
        return goalRepository.countActiveGoalsPerUser(userId);
    }
}
