package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class GoalDto {
    private Long id;
    private String title;
    private String description;
    private Long parentId;
    private GoalStatus status;
    private List<Long> skillsIds = new ArrayList<>();

    public GoalDto() {

    }

    public GoalDto(Goal savedGoal) {
        this.title = savedGoal.getTitle();
        this.description = savedGoal.getDescription();
        this.id = savedGoal.getId();
        this.parentId = getParentId();
        this.status = getStatus();
        this.skillsIds = getSkillsIds();
    }
}
