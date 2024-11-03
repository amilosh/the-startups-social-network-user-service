package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public record SkillOfferDto(
        @NotNull
        Long id,
        @NotNull
        Long skillId
) {

    public SkillOfferDto(@NotNull Long id, @NotNull Long skillId) {
        this.id = Objects.requireNonNull(id, "SkillOffer id is null");
        this.skillId = Objects.requireNonNull(skillId, "SkillOffer skillId is null");
    }
}
