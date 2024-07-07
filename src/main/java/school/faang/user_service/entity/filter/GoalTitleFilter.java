package school.faang.user_service.entity.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class GoalTitleFilter implements GoalFilters {
    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getTitle() != null;
    }

    @Override
    public void apply(Stream<Goal> goals, GoalFilterDto filters) {
        goals.filter(goal -> goal.getTitle().contains(filters.getTitle()));
    }
}
