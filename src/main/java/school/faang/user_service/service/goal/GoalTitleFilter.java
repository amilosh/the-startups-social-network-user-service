package school.faang.user_service.service.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class GoalTitleFilter implements GoalFilter {
    /**
     * This filter is applicable if the {@link GoalFilterDto} contains a non-null
     * title.
     *
     * @param filter the filter to check
     * @return true if the filter is applicable, false otherwise
     */
    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter.getTitle() != null;
    }

    /**
     * Filters the stream of goals to include only those whose titles contain
     * the specified substring from the filter.
     *
     * @param goals   the stream of goals to filter
     * @param filters the filter containing the title substring to match
     * @return a stream of goals with titles containing the specified substring
     */
    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters) {
        return goals.filter(goal -> goal.getTitle().contains(filters.getTitle()));
    }
}
