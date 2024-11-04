package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record SkillRequestDto(
        Long id,
        @NotNull @Positive Long skillId
) {
}
