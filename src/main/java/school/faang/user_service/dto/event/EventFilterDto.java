package school.faang.user_service.dto.event;

import lombok.Data;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventFilterDto {
    private String titlePattern;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String ownerName;
    private List<SkillDto> relatedSkills;
    private String location;
    private int maxAttendees;
}