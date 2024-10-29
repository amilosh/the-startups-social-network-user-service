package school.faang.user_service.dto.goal;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GoalDto {
    private Long id;
    private String title;
    private String description;
    private Long parentId;
    private String status;
    private List<Long> skillIds;
}
