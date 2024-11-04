package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RejectionDto(
        @NotNull @Positive Long recommendationId,
        @NotBlank String reason
) {
}
