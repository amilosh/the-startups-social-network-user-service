package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFilterDto {
    @Size(min = 1, max = 255, message = "The event name should be between 1 and 255 characters long")
    private String titlePattern;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Size(min = 1, max = 255, message = "The owner name of the event should be from 1 to 255 characters")
    private String ownerName;

    private List<SkillDto> relatedSkills;

    @Size(min = 1, max = 255, message = "The location of the event should be from 1 to 255 characters")
    private String location;

    private Integer maxAttendees;
}