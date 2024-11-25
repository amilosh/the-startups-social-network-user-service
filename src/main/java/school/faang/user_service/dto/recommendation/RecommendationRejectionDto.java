package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record RecommendationRejectionDto(
        @NotNull @Positive Long recommendationId,
        @NotBlank String reason
) {
}
