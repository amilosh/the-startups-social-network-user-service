package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EventDto {
    private Long id;

    @NotEmpty(message = "The event name should not be empty")
    @NotBlank(message = "The event name should not be blank")
    @Size(min = 1, max = 64, message = "The event name should be between 1 and 64 characters long")
    private String title;

    @NotNull(message = "The start date of the event should not be empty")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull(message = "The organizer's ID should not be empty")
    private Long ownerId;

    @NotEmpty(message = "The description of the event should not be empty")
    @NotBlank(message = "The description of the event should not be blank")
    @Size(min = 1, max = 4096, message = "The description of the event should be between 1 and 4096 characters long")
    private String description;

    private List<SkillDto> relatedSkills;

    @NotEmpty(message = "The event location should not be empty")
    @NotBlank(message = "The event location should not be blank")
    @Size(min = 1, max = 128, message = "The location of the event should be from 1 to 128 characters")
    private String location;

    private int maxAttendees;
}