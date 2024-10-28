package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;
    @NotEmpty
    @Size(min = 1, max = 64)
    private String title;
    @NotNull
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NotNull
    private Long ownerId;
    @NotEmpty
    @Size(min = 1, max = 4096)
    private String description;
    private List<SkillDto> relatedSkills;
    private String location;
    private Integer maxAttendees;
}
