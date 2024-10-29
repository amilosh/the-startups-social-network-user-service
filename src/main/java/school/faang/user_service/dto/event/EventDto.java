package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDto {

    private Long id;

    @NotNull(message = "title is empty")
    @NotBlank(message = "title is empty")
    @Size(max = 64, message = "title size more than 64 symbol")
    private String title;

    @NotNull(message = "start date is empty")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull(message = "owner ID is empty")
    private Long ownerId;

    @NotNull(message = "description is empty")
    @Size(max = 4096, message = "message length more than 4096 symbol")
    private String description;

    private List<SkillDto> relatedSkills;

    @Null
    @Size(max = 128, message = "location length more than 128 symbol")
    private String location;

    private int maxAttendees;
}