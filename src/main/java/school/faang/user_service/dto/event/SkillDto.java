package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.stereotype.Controller;

@Builder
public record SkillDto(
        Long id,
       @NotBlank String title
) {
}
