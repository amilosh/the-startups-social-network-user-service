package school.faang.user_service.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EventFilterDto {

    @Schema(description = "Pattern for filtering event titles (supports partial matches)", example = "Spring")
    private String titlePattern;
    @Schema(description = "Start date and time for filtering events (ISO 8601 format)", example = "2024-11-21T10:00:00")
    @PastOrPresent(message = "Start date must be in the past or present")
    private LocalDateTime startDate;
    @Schema(description = "End date and time for filtering events (ISO 8601 format)", example = "2024-11-22T18:00:00")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;
    @Schema(description = "Name of the event owner", example = "John Doe")
    @Size(max = 64, message = "Owner name must not exceed 64 characters")
    private String ownerName;
    @Schema(
            description = "List of skills related to the event",
            example = "[{\"id\": 1, \"title\": \"Java\"}, {\"id\": 2, \"title\": \"Spring\"}]"
    )
    private List<SkillDto> relatedSkills;
    @Schema(description = "Location of the event", example = "New York")
    private String location;
    @Schema(description = "Maximum number of attendees allowed for the event", example = "100")
    private Integer maxAttendees;
}