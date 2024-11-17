package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
@AllArgsConstructor
public class GoalFilterDto {

    private String title;
    private GoalStatus status;

    public GoalFilterDto() {

    }

    public boolean matches(Goal goal) {
        boolean matchesTitle = (title == null || goal.getTitle().contains(title));
        boolean matchesStatus = (status == null || goal.getStatus() == status);

        return matchesTitle && matchesStatus;
    }
}
