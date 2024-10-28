package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;

public record SkillRequestDto(@NotNull(message = "Id cannot be empty.")
                              Long id,
                              @NotNull(message = "requestId cannot be empty.")
                              Long requestId,
                              @NotNull(message = "skillId cannot be empty.")
                              Long skillId
) {
}
