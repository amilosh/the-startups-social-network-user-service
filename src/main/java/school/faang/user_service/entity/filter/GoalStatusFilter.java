package school.faang.user_service.entity.filter;

import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

public class GoalStatusFilter implements GoalFilters {

    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getTitle() != null;
    }

    @Override
    public void apply(Stream<Goal> goals, GoalFilterDto filters) {
        goals.filter(goal -> goal.getStatus().equals(filters.getStatus()));
    }
}
