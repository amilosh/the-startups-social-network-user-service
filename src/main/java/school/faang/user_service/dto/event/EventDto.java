package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventDto(
        Long id,
        @NotBlank String title,
        @NotNull @PastOrPresent LocalDateTime startDate,
        @NotNull @PastOrPresent LocalDateTime endDate,
        @NotNull Long ownerId,
        String description,
        List<SkillDto> relatedSkills,
        @NotBlank String location,
        int maxAttendees
) {

}
