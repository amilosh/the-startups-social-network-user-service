package school.faang.user_service.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private EventType type;
    @NotNull
    private EventStatus status;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
    @NotNull
    private Long ownerId;
    @NotEmpty
    @Size(min = 1, max = 4096)
    private String description;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<SkillDto> relatedSkills = new ArrayList<>();
    @NotNull
    @Size(min = 1, max = 128)
    private String location;
    @Min(1)
    private Integer maxAttendees = Integer.MAX_VALUE;
}
