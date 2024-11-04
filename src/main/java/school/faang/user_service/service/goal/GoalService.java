package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    public Goal findGoalById(Long id) {
        return goalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Goal not found"));
    }
}