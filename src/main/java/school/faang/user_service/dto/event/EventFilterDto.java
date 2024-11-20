package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EventFilterDto {
    @Size(min = 1, max = 255, message = "The event name pattern should be between 1 and 255 characters long")
    private String titlePattern;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Size(min = 1, max = 255, message = "The owner name pattern of the event should be from 1 to 255 characters")
    private String ownerName;

    private List<SkillDto> relatedSkills;

    @Size(min = 1, max = 255, message = "The location pattern of the event should be from 1 to 255 characters")
    private String location;

    private Integer maxAttendees;
}