package school.faang.user_service.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalStatusDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

@Component
public class GoalSpecification {

    public static Specification<Goal> build(GoalFilterDto filter) {
        return hasTitle(filter.title())
                .and(hasDescription(filter.description()));
    }

    private static Specification<Goal> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? cb.conjunction()
                        : cb.like(root.get("title"), "%" + title + "%");
    }

    private static Specification<Goal> hasDescription(String description) {
        return (root, query, cb) ->
                description == null ? cb.conjunction()
                        : cb.like(root.get("description"), "%" + description + "%");
    }

    /*private static Specification<Goal> hasStatus(GoalStatusDto status) {
        return ((root, query, cb) -> {
            status == null ? cb.conjunction()
            : cb.equal(root.get("status"), )
        }
    }*/
}
