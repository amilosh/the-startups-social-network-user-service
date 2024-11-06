package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillOfferDto {

    @Positive(message = "Id must be a positive number")
    private Long id;

    @NotNull(message = "Id must be a positive number")
    @Positive(message = "Id must be a positive number")
    private Long skillId;

    @NotNull(message = "Id must be a positive number")
    @Positive(message = "Id must be a positive number")
    private Long recommendationId;
}
