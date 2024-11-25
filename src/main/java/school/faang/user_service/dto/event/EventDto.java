package school.faang.user_service.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EventDto {
    @Schema(description = "Unique identifier of the event", example = "1")
    private Long id;

    @Schema(description = "Title of the event", example = "Java Workshop")
    @NotBlank(message = "Title is required")
    @Size(max = 64, message = "Title must not exceed 64 characters")
    private String title;

    @Schema(description = "Type of the event", example = "MEETING")
    @NotNull(message = "Event type is required")
    private EventType type;

    @Schema(description = "Status of the event", example = "PLANNED")
    @NotNull(message = "Event status is required")
    private EventStatus status;

    @Schema(description = "Start date and time of the event", example = "2024-12-01T14:00:00")
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @Schema(description = "End date and time of the event", example = "2024-12-01T18:00:00")
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @Schema(description = "ID of the event owner", example = "42")
    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @Schema(description = "Description of the event", example = "A hands-on workshop on advanced Java concepts.")
    @Size(max = 4096, message = "Description must not exceed 4096 characters")
    @NotBlank(message = "Description is required")
    private String description;

    @NotNull
    @Size(min = 1, message = "At least one skill is required")
    @Schema(description = "List of skills related to the event", example = "[{\"id\":1, \"title\":\"Java\"}]")
    private List<@Valid SkillDto> relatedSkills;

    @Schema(description = "Location of the event", example = "New York, NY")
    @Size(min = 1, max = 128, message = "Location must not exceed 128 characters")
    private String location;

    @Schema(description = "Maximum number of attendees allowed", example = "100")
    @Min(value = 1, message = "Maximum attendees must be at least 1")
    private int maxAttendees;
}