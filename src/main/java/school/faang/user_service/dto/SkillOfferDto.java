package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
