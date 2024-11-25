package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Validated
public class EventDto {
    @NotNull(message = "Event id must not be null")
    private Long id;
    @NotNull(message = "Title must not be null")
    @NotBlank(message = "Title must not be blank")
    @Size(max = 128, message = "Title must not exceed 128 characters")
    private String title;
    @NotNull(message = "StartDate must not be null")
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NotNull(message = "Request ownerId must not be null")
    private Long ownerId;
    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be blank")
    @Size(max = 4096, message = "Description must not exceed 4096 characters")
    private String description;
    private List<Long> relatedSkillIds;
    private String location;
    private int maxAttendees;
}
